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
import com.star.reward.domain.pointrecord.model.constant.PointRecordConstants;
import com.star.reward.domain.pointrecord.model.entity.PointRecordBO;
import com.star.reward.domain.pointrecord.repository.PointRecordRepository;
import com.star.reward.domain.taskinstance.repository.TaskInstanceRepository;
import com.star.reward.domain.taskinstance.service.ExecutionRecordParser;
import com.star.reward.domain.taskinstance.service.PointsCalculationService;
import com.star.reward.domain.tasktemplate.model.entity.TaskTemplateBO;
import com.star.reward.domain.tasktemplate.repository.TaskTemplateRepository;
import com.star.reward.domain.userinventory.model.entity.UserInventoryBO;
import com.star.reward.domain.userinventory.model.valueobject.InventoryType;
import com.star.reward.domain.userinventory.repository.UserInventoryRepository;
import com.star.reward.application.command.StartTaskCommand;
import com.star.reward.interfaces.rest.dto.response.TaskExecutionResponse;
import com.star.reward.shared.context.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.star.reward.domain.shared.constant.RewardConstants;
import com.star.reward.domain.shared.util.RewardNoGenerator;
import com.star.reward.domain.taskinstance.model.constant.TaskInstanceConstants;
import com.star.reward.domain.userinventory.model.constant.UserInventoryConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    private final PointRecordRepository pointRecordRepository;
    private final PointsCalculationService pointsCalculationService;

    /**
     * 根据实例编号获取任务执行详情，校验 executeById=当前用户
     */
    public TaskExecutionResponse getTaskExecutionByInstanceNo(String instanceNo) {
        CurrentUserContext user = CurrentUserContext.get();
        TaskInstanceBO instance = taskInstanceRepository.findByInstanceNo(instanceNo)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "任务执行记录不存在"));
        if (!user.getUserId().equals(instance.getExecuteById())) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "无权访问该任务执行记录");
        }
        return toResponse(instance);
    }

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
    public TaskExecutionResponse startTask(StartTaskCommand command) {
        CurrentUserContext user = CurrentUserContext.get();

        List<TaskInstanceBO> runningTasks = taskInstanceRepository
                .findByExecuteByIdAndState(user.getUserId(), InstanceState.RUNNING);
        List<TaskInstanceBO> pausedTasks = taskInstanceRepository
                .findByExecuteByIdAndState(user.getUserId(), InstanceState.PAUSED);

        if (!runningTasks.isEmpty() || !pausedTasks.isEmpty()) {
            throw new BusinessException(ResultCode.TASK_ALREADY_RUNNING.getCode(),
                    "一次只能执行一个任务！请先完成当前任务。");
        }

        TaskTemplateBO template = taskTemplateRepository.findById(command.getTaskTemplateId())
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "任务模板不存在"));

        LocalDateTime now = LocalDateTime.now();
        TaskInstanceBO instance = TaskInstanceBO.createFromTemplate(template,
                RewardNoGenerator.generate(TaskInstanceConstants.INSTANCE_NO_PREFIX),
                user.getUserNo(), user.getUserId(), now);

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
        instance.appendExecutionRecord(ExecutionRecordVO.of(ExecutionAction.PAUSE, now));
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
        instance.appendExecutionRecord(ExecutionRecordVO.of(ExecutionAction.RESUME, now));
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
        instance.appendExecutionRecord(ExecutionRecordVO.of(ExecutionAction.END, now));
        instance.setEndTime(now);
        instance.setInstanceState(InstanceState.END);
        instance.setUpdateTime(now);

        List<ExecutionInterval> executionIntervals =
                ExecutionRecordParser.toExecutionIntervals(instance.getExecutionRecords());
        Map<Integer, List<PointsConversionSegment>> multiplierToSegments = Collections.emptyMap();
        int pointsPerMin = instance.getMinUnitPoint() != null
                ? instance.getMinUnitPoint()
                : RewardConstants.DEFAULT_POINTS_PER_MINUTE;

        PointsCalculationResult result = pointsCalculationService.calculate(
                executionIntervals, multiplierToSegments, pointsPerMin);

        instance.setPointsCalculationSnapshot(
                PointsCalculationSnapshot.of(result, pointsPerMin, now));

        TaskInstanceBO updated = taskInstanceRepository.update(instance);

        addUserPoints(user, result.getTotalPoints(), "完成任务: " + instance.getName());

        PointRecordBO earnRecord = PointRecordBO.createEarn(
                RewardNoGenerator.generate(PointRecordConstants.RECORD_NO_PREFIX),
                result.getTotalPoints(), user.getUserNo(), user.getUserId(),
                instance.getInstanceNo(), "完成任务: " + instance.getName(), now);
        pointRecordRepository.save(earnRecord);

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
        instance.setRemark(TaskInstanceConstants.REMARK_CANCELLED);

        TaskInstanceBO updated = taskInstanceRepository.update(instance);

        TaskExecutionResponse response = toResponse(updated);
        response.setStatus(TaskInstanceConstants.STATUS_CANCELLED);
        response.setActualReward(BigDecimal.ZERO);
        response.setPointsDetails(Collections.emptyList());
        return response;
    }

    private void addUserPoints(CurrentUserContext user, BigDecimal amount, String description) {
        List<UserInventoryBO> pointInventories = userInventoryRepository
                .findByBelongToIdAndType(user.getUserId(), InventoryType.POINT);

        if (pointInventories.isEmpty()) {
            UserInventoryBO inventory = UserInventoryBO.createPointInventory(
                    RewardNoGenerator.generate(UserInventoryConstants.INVENTORY_NO_PREFIX),
                    user.getUserNo(), user.getUserId(), amount, LocalDateTime.now());
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
                status = TaskInstanceConstants.STATUS_RUNNING;
                break;
            case PAUSED:
                status = TaskInstanceConstants.STATUS_PAUSED;
                break;
            case END:
                status = bo.getRemark() != null && bo.getRemark().contains(TaskInstanceConstants.CANCELLED_KEYWORD)
                        ? TaskInstanceConstants.STATUS_CANCELLED
                        : TaskInstanceConstants.STATUS_COMPLETED;
                break;
            default:
                status = TaskInstanceConstants.STATUS_UNKNOWN;
        }

        List<ExecutionRecordVO> records = bo.getExecutionRecords();
        TaskExecutionResponse response = TaskExecutionResponse.builder()
                .id(bo.getId())
                .executionNo(bo.getInstanceNo())
                .userNo(bo.getExecuteBy())
                .taskName(bo.getName())
                .startTime(bo.getStartTime() != null ? bo.getStartTime().toEpochSecond(ZoneOffset.of("+8")) : null)
                .endTime(bo.getEndTime() != null ? bo.getEndTime().toEpochSecond(ZoneOffset.of("+8")) : null)
                .status(status)
                .createTime(bo.getCreateTime())
                .executionRecords(records)
                .totalPausedDuration(ExecutionRecordParser.computeTotalPausedDuration(records))
                .totalExecutionDuration(ExecutionRecordParser.computeTotalExecutionDuration(
                        records, bo.getStartTime(), LocalDateTime.now()))
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
                int pointsPerMin = bo.getMinUnitPoint() != null ? bo.getMinUnitPoint() : RewardConstants.DEFAULT_POINTS_PER_MINUTE;
                PointsCalculationResult result = pointsCalculationService.calculate(
                        intervals, multiplierToSegments, pointsPerMin);
                response.setActualReward(result.getTotalPoints());
                response.setPointsDetails(result.getDetails());
                response.setActualDuration(calculateTotalDurationMinutes(result.getDetails()));
                PointsCalculationSnapshot backfill = PointsCalculationSnapshot.of(
                        result, pointsPerMin, LocalDateTime.now());
                bo.setPointsCalculationSnapshot(backfill);
                taskInstanceRepository.update(bo);
            }
        }
        return response;
    }
}
