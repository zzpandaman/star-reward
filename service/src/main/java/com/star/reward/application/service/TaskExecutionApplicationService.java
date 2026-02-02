package com.star.reward.application.service;

import com.star.common.exception.BusinessException;
import com.star.common.page.PageResponse;
import com.star.common.result.ResultCode;
import com.star.reward.domain.taskinstance.model.entity.TaskInstanceBO;
import com.star.reward.domain.taskinstance.model.valueobject.InstanceState;
import com.star.reward.domain.taskinstance.repository.TaskInstanceRepository;
import com.star.reward.domain.tasktemplate.model.entity.TaskTemplateBO;
import com.star.reward.domain.tasktemplate.repository.TaskTemplateRepository;
import com.star.reward.domain.userinventory.model.entity.UserInventoryBO;
import com.star.reward.domain.userinventory.model.valueobject.InventoryType;
import com.star.reward.domain.userinventory.repository.UserInventoryRepository;
import com.star.reward.interfaces.rest.dto.request.StartTaskRequest;
import com.star.reward.interfaces.rest.dto.response.TaskExecutionResponse;
import com.star.reward.shared.context.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 任务执行应用服务
 */
@Service
@RequiredArgsConstructor
public class TaskExecutionApplicationService {
    
    private final TaskInstanceRepository taskInstanceRepository;
    private final TaskTemplateRepository taskTemplateRepository;
    private final UserInventoryRepository userInventoryRepository;
    
    /**
     * 获取任务执行列表
     */
    public PageResponse<TaskExecutionResponse> getTaskExecutions() {
        CurrentUserContext user = CurrentUserContext.get();
        List<TaskInstanceBO> instances = taskInstanceRepository.findByExecuteById(user.getUserId());
        List<TaskExecutionResponse> responses = instances.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return PageResponse.of(responses, responses.size(), 1, responses.size());
    }
    
    /**
     * 开始任务
     */
    @Transactional
    public TaskExecutionResponse startTask(StartTaskRequest request) {
        CurrentUserContext user = CurrentUserContext.get();
        
        // 检查是否有正在执行或暂停的任务
        List<TaskInstanceBO> runningTasks = taskInstanceRepository
                .findByExecuteByIdAndState(user.getUserId(), InstanceState.RUNNING);
        List<TaskInstanceBO> pausedTasks = taskInstanceRepository
                .findByExecuteByIdAndState(user.getUserId(), InstanceState.PAUSED);
        
        if (!runningTasks.isEmpty() || !pausedTasks.isEmpty()) {
            throw new BusinessException(ResultCode.TASK_ALREADY_RUNNING.getCode(), 
                    "一次只能执行一个任务！请先完成当前任务。");
        }
        
        // 获取任务模板
        TaskTemplateBO template = taskTemplateRepository.findById(request.getTaskTemplateId())
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "任务模板不存在"));
        
        // 创建任务实例
        TaskInstanceBO instance = TaskInstanceBO.builder()
                .instanceNo(generateInstanceNo())
                .templateNo(template.getTemplateNo())
                .name(template.getName())
                .description(template.getDescription())
                .minUnitPoint(template.getMinUnitPoint() != null ? template.getMinUnitPoint().intValue() : 1)
                .minUnit(template.getMinUnit())
                .publishBy(template.getPublishBy())
                .publishById(template.getPublishById())
                .startTime(LocalDateTime.now())
                .instanceState(InstanceState.RUNNING)
                .executeBy(user.getUserNo())
                .executeById(user.getUserId())
                .isPreset(template.getIsPreset())
                .isDeleted(false)
                .createBy(user.getUserNo())
                .createById(user.getUserId())
                .createTime(LocalDateTime.now())
                .build();
        
        TaskInstanceBO saved = taskInstanceRepository.save(instance);
        return toResponse(saved);
    }
    
    /**
     * 暂停任务
     */
    @Transactional
    public TaskExecutionResponse pauseTask(Long id) {
        TaskInstanceBO instance = taskInstanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "任务执行记录不存在"));
        
        if (instance.getInstanceState() != InstanceState.RUNNING) {
            throw new BusinessException(ResultCode.TASK_NOT_RUNNING.getCode(), 
                    ResultCode.TASK_NOT_RUNNING.getMessage());
        }
        
        instance.setInstanceState(InstanceState.PAUSED);
        // 记录暂停时间到 attributes 字段
        instance.setAttributes(buildPauseAttributes(instance.getAttributes()));
        instance.setUpdateTime(LocalDateTime.now());
        
        TaskInstanceBO updated = taskInstanceRepository.update(instance);
        return toResponse(updated);
    }
    
    /**
     * 恢复任务
     */
    @Transactional
    public TaskExecutionResponse resumeTask(Long id) {
        TaskInstanceBO instance = taskInstanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "任务执行记录不存在"));
        
        if (instance.getInstanceState() != InstanceState.PAUSED) {
            throw new BusinessException(ResultCode.TASK_NOT_PAUSED.getCode(), 
                    ResultCode.TASK_NOT_PAUSED.getMessage());
        }
        
        instance.setInstanceState(InstanceState.RUNNING);
        // 更新 attributes，记录恢复时间
        instance.setAttributes(buildResumeAttributes(instance.getAttributes()));
        instance.setUpdateTime(LocalDateTime.now());
        
        TaskInstanceBO updated = taskInstanceRepository.update(instance);
        return toResponse(updated);
    }
    
    /**
     * 完成任务
     */
    @Transactional
    public TaskExecutionResponse completeTask(Long id) {
        CurrentUserContext user = CurrentUserContext.get();
        
        TaskInstanceBO instance = taskInstanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "任务执行记录不存在"));
        
        if (instance.getInstanceState() == InstanceState.END) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "任务已结束");
        }
        
        LocalDateTime now = LocalDateTime.now();
        instance.setEndTime(now);
        instance.setInstanceState(InstanceState.END);
        instance.setUpdateTime(now);
        
        // 计算积分（简化：1分钟1积分）
        long durationMinutes = Duration.between(instance.getStartTime(), now).toMinutes();
        BigDecimal reward = BigDecimal.valueOf(durationMinutes);
        
        TaskInstanceBO updated = taskInstanceRepository.update(instance);
        
        // 增加用户积分
        addUserPoints(user, reward, "完成任务: " + instance.getName());
        
        TaskExecutionResponse response = toResponse(updated);
        response.setActualDuration(BigDecimal.valueOf(durationMinutes));
        response.setActualReward(reward);
        return response;
    }
    
    /**
     * 取消任务
     */
    @Transactional
    public TaskExecutionResponse cancelTask(Long id) {
        TaskInstanceBO instance = taskInstanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "任务执行记录不存在"));
        
        if (instance.getInstanceState() == InstanceState.END) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "任务已结束");
        }
        
        LocalDateTime now = LocalDateTime.now();
        instance.setEndTime(now);
        instance.setInstanceState(InstanceState.END);
        instance.setUpdateTime(now);
        instance.setRemark("已取消");
        
        TaskInstanceBO updated = taskInstanceRepository.update(instance);
        
        TaskExecutionResponse response = toResponse(updated);
        response.setStatus("cancelled");
        response.setActualReward(BigDecimal.ZERO);
        return response;
    }
    
    /**
     * 增加用户积分
     */
    private void addUserPoints(CurrentUserContext user, BigDecimal amount, String description) {
        // 查找用户积分库存
        List<UserInventoryBO> pointInventories = userInventoryRepository
                .findByBelongToIdAndType(user.getUserId(), InventoryType.POINT);
        
        if (pointInventories.isEmpty()) {
            // 创建积分库存
            UserInventoryBO inventory = UserInventoryBO.builder()
                    .inventoryNo(generateInventoryNo())
                    .inventoryType(InventoryType.POINT)
                    .name("积分")
                    .description("用户积分")
                    .quantity(amount)
                    .unit("点")
                    .belongTo(user.getUserNo())
                    .belongToId(user.getUserId())
                    .isDeleted(false)
                    .createBy(user.getUserNo())
                    .createById(user.getUserId())
                    .createTime(LocalDateTime.now())
                    .build();
            userInventoryRepository.save(inventory);
        } else {
            // 更新积分
            UserInventoryBO inventory = pointInventories.get(0);
            inventory.setQuantity(inventory.getQuantity().add(amount));
            inventory.setUpdateBy(user.getUserNo());
            inventory.setUpdateById(user.getUserId());
            inventory.setUpdateTime(LocalDateTime.now());
            userInventoryRepository.update(inventory);
        }
    }
    
    /**
     * 生成实例编号
     */
    private String generateInstanceNo() {
        return "EXE" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
    
    /**
     * 生成库存编号
     */
    private String generateInventoryNo() {
        return "INV" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
    
    /**
     * 构建暂停属性
     */
    private String buildPauseAttributes(String existingAttributes) {
        long pausedAt = System.currentTimeMillis() / 1000;
        return "{\"pausedAt\":" + pausedAt + "}";
    }
    
    /**
     * 构建恢复属性
     */
    private String buildResumeAttributes(String existingAttributes) {
        return "{}";
    }
    
    /**
     * 转换为响应
     */
    private TaskExecutionResponse toResponse(TaskInstanceBO bo) {
        String status;
        switch (bo.getInstanceState()) {
            case RUNNING:
                status = "running";
                break;
            case PAUSED:
                status = "paused";
                break;
            case END:
                status = bo.getRemark() != null && bo.getRemark().contains("取消") ? "cancelled" : "completed";
                break;
            default:
                status = "unknown";
        }
        
        return TaskExecutionResponse.builder()
                .id(bo.getId())
                .executionNo(bo.getInstanceNo())
                .userNo(bo.getExecuteBy())
                .taskName(bo.getName())
                .startTime(bo.getStartTime() != null ? bo.getStartTime().toEpochSecond(ZoneOffset.of("+8")) : null)
                .endTime(bo.getEndTime() != null ? bo.getEndTime().toEpochSecond(ZoneOffset.of("+8")) : null)
                .status(status)
                .createTime(bo.getCreateTime())
                .build();
    }
}
