package com.star.reward.application.service;

import com.star.common.exception.BusinessException;
import com.star.common.page.PageResponse;
import com.star.common.result.ResultCode;
import com.star.reward.domain.taskinstance.model.entity.TaskInstanceBO;
import com.star.reward.domain.taskinstance.model.valueobject.ExecutionAction;
import com.star.reward.domain.taskinstance.model.valueobject.ExecutionInterval;
import com.star.reward.domain.taskinstance.model.valueobject.ExecutionRecordVO;
import com.star.reward.domain.taskinstance.model.valueobject.InstanceState;
import com.star.reward.domain.taskinstance.model.valueobject.PointsCalculationResult;
import com.star.reward.domain.taskinstance.model.valueobject.PointsCalculationSnapshot;
import com.star.reward.domain.taskinstance.model.valueobject.PointsConversionSegment;
import com.star.reward.domain.taskinstance.model.valueobject.PointsDetailItem;
import com.star.reward.domain.taskinstance.repository.TaskInstanceRepository;
import com.star.reward.domain.taskinstance.service.ExecutionRecordParser;
import com.star.reward.domain.taskinstance.service.PointsCalculationService;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    private final PointsCalculationService pointsCalculationService;

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

        List<TaskInstanceBO> runningTasks = taskInstanceRepository
                .findByExecuteByIdAndState(user.getUserId(), InstanceState.RUNNING);
        List<TaskInstanceBO> pausedTasks = taskInstanceRepository
                .findByExecuteByIdAndState(user.getUserId(), InstanceState.PAUSED);

        if (!runningTasks.isEmpty() || !pausedTasks.isEmpty()) {
            throw new BusinessException(ResultCode.TASK_ALREADY_RUNNING.getCode(),
                    "一次只能执行一个任务！请先完成当前任务。");
        }

        TaskTemplateBO template = taskTemplateRepository.findById(request.getTaskTemplateId())
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "任务模板不存在"));

        LocalDateTime now = LocalDateTime.now();
        TaskInstanceBO instance = TaskInstanceBO.builder()
                .instanceNo(generateInstanceNo())
                .templateNo(template.getTemplateNo())
                .name(template.getName())
                .description(template.getDescription())
                .minUnitPoint(template.getMinUnitPoint() != null ? template.getMinUnitPoint().intValue() : 1)
                .minUnit(template.getMinUnit())
                .publishBy(template.getPublishBy())
                .publishById(template.getPublishById())
                .startTime(now)
                .instanceState(InstanceState.RUNNING)
                .executeBy(user.getUserNo())
                .executeById(user.getUserId())
                .isPreset(template.getIsPreset())
                .isDeleted(false)
                .createBy(user.getUserNo())
                .createById(user.getUserId())
                .createTime(now)
                .build();

        instance.appendExecutionRecord(ExecutionRecordVO.builder()
                .action(ExecutionAction.START)
                .actionTime(now)
                .build());

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

        LocalDateTime now = LocalDateTime.now();
        instance.setInstanceState(InstanceState.PAUSED);
        instance.appendExecutionRecord(ExecutionRecordVO.builder()
                .action(ExecutionAction.PAUSE)
                .actionTime(now)
                .build());
        instance.setUpdateTime(now);

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

        LocalDateTime now = LocalDateTime.now();
        instance.setInstanceState(InstanceState.RUNNING);
        instance.appendExecutionRecord(ExecutionRecordVO.builder()
                .action(ExecutionAction.RESUME)
                .actionTime(now)
                .build());
        instance.setUpdateTime(now);

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
        instance.appendExecutionRecord(ExecutionRecordVO.builder()
                .action(ExecutionAction.END)
                .actionTime(now)
                .build());
        instance.setEndTime(now);
        instance.setInstanceState(InstanceState.END);
        instance.setUpdateTime(now);

        List<ExecutionInterval> executionIntervals =
                ExecutionRecordParser.toExecutionIntervals(instance.getExecutionRecords());
        Map<Integer, List<PointsConversionSegment>> multiplierToSegments = Collections.emptyMap();
        int pointsPerMin = instance.getMinUnitPoint() != null ? instance.getMinUnitPoint() : 1;

        PointsCalculationResult result = pointsCalculationService.calculate(
                executionIntervals, multiplierToSegments, pointsPerMin);

        PointsCalculationSnapshot snapshot = PointsCalculationSnapshot.builder()
                .totalPoints(result.getTotalPoints())
                .pointsPerMinute(pointsPerMin)
                .calculatedAt(now)
                .details(result.getDetails())
                .build();
        instance.setPointsCalculationSnapshot(snapshot);

        TaskInstanceBO updated = taskInstanceRepository.update(instance);

        addUserPoints(user, result.getTotalPoints(), "完成任务: " + instance.getName());

        TaskExecutionResponse response = toResponse(updated);
        response.setActualDuration(calculateTotalDurationMinutes(result.getDetails()));
        response.setActualReward(result.getTotalPoints());
        response.setPointsDetails(result.getDetails());
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
        response.setPointsDetails(Collections.emptyList());
        return response;
    }

    private void addUserPoints(CurrentUserContext user, BigDecimal amount, String description) {
        List<UserInventoryBO> pointInventories = userInventoryRepository
                .findByBelongToIdAndType(user.getUserId(), InventoryType.POINT);

        if (pointInventories.isEmpty()) {
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
            UserInventoryBO inventory = pointInventories.get(0);
            inventory.setQuantity(inventory.getQuantity().add(amount));
            inventory.setUpdateBy(user.getUserNo());
            inventory.setUpdateById(user.getUserId());
            inventory.setUpdateTime(LocalDateTime.now());
            userInventoryRepository.update(inventory);
        }
    }

    private String generateInstanceNo() {
        return "EXE" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    private String generateInventoryNo() {
        return "INV" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    private BigDecimal calculateTotalDurationMinutes(List<PointsDetailItem> details) {
        if (details == null || details.isEmpty()) {
            return BigDecimal.ZERO;
        }
        long total = details.stream()
                .mapToLong(d -> d.getDurationMinutes() != null ? d.getDurationMinutes() : 0L)
                .sum();
        return BigDecimal.valueOf(total);
    }

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

        TaskExecutionResponse response = TaskExecutionResponse.builder()
                .id(bo.getId())
                .executionNo(bo.getInstanceNo())
                .userNo(bo.getExecuteBy())
                .taskName(bo.getName())
                .startTime(bo.getStartTime() != null ? bo.getStartTime().toEpochSecond(ZoneOffset.of("+8")) : null)
                .endTime(bo.getEndTime() != null ? bo.getEndTime().toEpochSecond(ZoneOffset.of("+8")) : null)
                .status(status)
                .createTime(bo.getCreateTime())
                .executionRecords(bo.getExecutionRecords())
                .build();

        if (bo.getInstanceState() == InstanceState.END) {
            PointsCalculationSnapshot snapshot = bo.getPointsCalculationSnapshot();
            if (snapshot != null) {
                response.setActualReward(snapshot.getTotalPoints());
                response.setPointsDetails(snapshot.getDetails());
                response.setActualDuration(calculateTotalDurationMinutes(snapshot.getDetails()));
            } else {
                List<ExecutionInterval> intervals =
                        ExecutionRecordParser.toExecutionIntervals(bo.getExecutionRecords());
                Map<Integer, List<PointsConversionSegment>> multiplierToSegments = Collections.emptyMap();
                int pointsPerMin = bo.getMinUnitPoint() != null ? bo.getMinUnitPoint() : 1;
                PointsCalculationResult result = pointsCalculationService.calculate(
                        intervals, multiplierToSegments, pointsPerMin);
                response.setActualReward(result.getTotalPoints());
                response.setPointsDetails(result.getDetails());
                response.setActualDuration(calculateTotalDurationMinutes(result.getDetails()));
                PointsCalculationSnapshot backfill = PointsCalculationSnapshot.builder()
                        .totalPoints(result.getTotalPoints())
                        .pointsPerMinute(pointsPerMin)
                        .calculatedAt(LocalDateTime.now())
                        .details(result.getDetails())
                        .build();
                bo.setPointsCalculationSnapshot(backfill);
                taskInstanceRepository.update(bo);
            }
        }
        return response;
    }
}
