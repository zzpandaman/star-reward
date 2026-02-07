package com.star.reward.application.service;

import com.star.common.exception.BusinessException;
import com.star.common.result.ResultCode;
import com.star.reward.domain.taskinstance.model.entity.TaskInstanceBO;
import com.star.reward.domain.taskinstance.model.constant.TaskInstanceConstants;
import com.star.reward.domain.taskinstance.model.valueobject.ExecutionAction;
import com.star.reward.domain.taskinstance.model.valueobject.ExecutionRecordVO;
import com.star.reward.domain.taskinstance.model.valueobject.InstanceState;
import com.star.reward.domain.taskinstance.model.valueobject.PointsCalculationResult;
import com.star.reward.domain.taskinstance.repository.TaskInstanceRepository;
import com.star.reward.domain.taskinstance.service.PointsCalculationService;
import com.star.reward.domain.tasktemplate.model.entity.TaskTemplateBO;
import com.star.reward.domain.tasktemplate.repository.TaskTemplateRepository;
import com.star.reward.domain.pointrecord.repository.PointRecordRepository;
import com.star.reward.domain.userinventory.repository.UserInventoryRepository;
import com.star.reward.application.command.StartTaskCommand;
import com.star.reward.application.command.TaskExecutionQueryCommand;
import com.star.reward.interfaces.rest.dto.response.TaskExecutionResponse;
import com.star.reward.shared.context.CurrentUserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TaskExecutionApplicationService Mock 测试
 */
@ExtendWith(MockitoExtension.class)
class TaskExecutionApplicationServiceTest {

    private static final Long USER_ID = 100L;
    private static final String USER_NO = "user001";
    private static final Long TEMPLATE_ID = 1L;
    private static final Long INSTANCE_ID = 10L;

    @Mock
    private TaskInstanceRepository taskInstanceRepository;
    @Mock
    private TaskTemplateRepository taskTemplateRepository;
    @Mock
    private UserInventoryRepository userInventoryRepository;
    @Mock
    private PointRecordRepository pointRecordRepository;
    @Mock
    private PointsCalculationService pointsCalculationService;

    @InjectMocks
    private TaskExecutionApplicationService service;

    private CurrentUserContext userContext;

    @BeforeEach
    void setUp() {
        userContext = new CurrentUserContext();
        userContext.setUserId(USER_ID);
        userContext.setUserNo(USER_NO);
        CurrentUserContext.set(userContext);
    }

    @AfterEach
    void tearDown() {
        CurrentUserContext.clear();
    }

    @Test
    void startTask_成功() {
        when(taskInstanceRepository.findByExecuteByIdAndState(USER_ID, InstanceState.RUNNING))
                .thenReturn(Collections.emptyList());
        when(taskInstanceRepository.findByExecuteByIdAndState(USER_ID, InstanceState.PAUSED))
                .thenReturn(Collections.emptyList());
        TaskTemplateBO template = TaskTemplateBO.builder()
                .id(TEMPLATE_ID)
                .templateNo("TMP001")
                .name("测试任务")
                .minUnitPoint(BigDecimal.ONE)
                .build();
        when(taskTemplateRepository.findById(TEMPLATE_ID)).thenReturn(Optional.of(template));

        TaskInstanceBO savedInstance = TaskInstanceBO.builder()
                .id(INSTANCE_ID)
                .instanceNo("EXE001")
                .name("测试任务")
                .instanceState(InstanceState.RUNNING)
                .build();
        when(taskInstanceRepository.save(any(TaskInstanceBO.class))).thenReturn(savedInstance);

        StartTaskCommand command = new StartTaskCommand();
        command.setTaskTemplateId(TEMPLATE_ID);

        TaskExecutionResponse response = service.startTask(command);

        ArgumentCaptor<TaskInstanceBO> captor = ArgumentCaptor.forClass(TaskInstanceBO.class);
        verify(taskInstanceRepository).save(captor.capture());
        TaskInstanceBO captured = captor.getValue();
        assertThat(captured.getExecutionRecords()).hasSize(1);
        assertThat(captured.getExecutionRecords().get(0).getAction()).isEqualTo(ExecutionAction.START);
        assertThat(response.getExecutionNo()).isEqualTo("EXE001");
    }

    @Test
    void startTask_已有运行中任务() {
        TaskInstanceBO running = TaskInstanceBO.builder().id(1L).instanceState(InstanceState.RUNNING).build();
        when(taskInstanceRepository.findByExecuteByIdAndState(USER_ID, InstanceState.RUNNING))
                .thenReturn(Collections.singletonList(running));
        when(taskInstanceRepository.findByExecuteByIdAndState(USER_ID, InstanceState.PAUSED))
                .thenReturn(Collections.emptyList());

        StartTaskCommand command = new StartTaskCommand();
        command.setTaskTemplateId(TEMPLATE_ID);

        assertThatThrownBy(() -> service.startTask(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("一次只能执行一个任务");
        verify(taskInstanceRepository, never()).save(any());
    }

    @Test
    void pauseTask_成功() {
        TaskInstanceBO instance = createRunningInstance();
        when(taskInstanceRepository.findById(INSTANCE_ID)).thenReturn(Optional.of(instance));
        when(taskInstanceRepository.update(any(TaskInstanceBO.class))).thenAnswer(i -> i.getArgument(0));

        TaskExecutionResponse response = service.pauseTask(INSTANCE_ID);

        ArgumentCaptor<TaskInstanceBO> captor = ArgumentCaptor.forClass(TaskInstanceBO.class);
        verify(taskInstanceRepository).update(captor.capture());
        assertThat(captor.getValue().getExecutionRecords()).hasSize(2);
        assertThat(captor.getValue().getExecutionRecords().get(1).getAction()).isEqualTo(ExecutionAction.PAUSE);
        assertThat(response.getStatus()).isEqualTo(TaskInstanceConstants.STATUS_PAUSED);
    }

    @Test
    void pauseTask_非运行中() {
        TaskInstanceBO instance = createPausedInstance();
        when(taskInstanceRepository.findById(INSTANCE_ID)).thenReturn(Optional.of(instance));

        assertThatThrownBy(() -> service.pauseTask(INSTANCE_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ResultCode.TASK_NOT_RUNNING.getMessage());
        verify(taskInstanceRepository, never()).update(any());
    }

    @Test
    void resumeTask_成功() {
        TaskInstanceBO instance = createPausedInstance();
        when(taskInstanceRepository.findById(INSTANCE_ID)).thenReturn(Optional.of(instance));
        when(taskInstanceRepository.update(any(TaskInstanceBO.class))).thenAnswer(i -> i.getArgument(0));

        TaskExecutionResponse response = service.resumeTask(INSTANCE_ID);

        ArgumentCaptor<TaskInstanceBO> captor = ArgumentCaptor.forClass(TaskInstanceBO.class);
        verify(taskInstanceRepository).update(captor.capture());
        assertThat(captor.getValue().getExecutionRecords()).hasSize(3);
        assertThat(captor.getValue().getExecutionRecords().get(2).getAction()).isEqualTo(ExecutionAction.RESUME);
        assertThat(response.getStatus()).isEqualTo(TaskInstanceConstants.STATUS_RUNNING);
    }

    @Test
    void completeTask_成功() {
        TaskInstanceBO instance = createRunningInstance();
        when(taskInstanceRepository.findById(INSTANCE_ID)).thenReturn(Optional.of(instance));
        when(taskInstanceRepository.update(any(TaskInstanceBO.class))).thenAnswer(i -> i.getArgument(0));
        when(userInventoryRepository.findByBelongToIdAndType(USER_ID, com.star.reward.domain.userinventory.model.valueobject.InventoryType.POINT))
                .thenReturn(Collections.emptyList());
        when(userInventoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PointsCalculationResult calcResult = PointsCalculationResult.of(
                BigDecimal.valueOf(60), Collections.emptyList());
        when(pointsCalculationService.calculate(any(), any(), any())).thenReturn(calcResult);

        TaskExecutionResponse response = service.completeTask(INSTANCE_ID);

        verify(pointsCalculationService).calculate(any(), eq(Collections.emptyMap()), any());
        ArgumentCaptor<TaskInstanceBO> captor = ArgumentCaptor.forClass(TaskInstanceBO.class);
        verify(taskInstanceRepository).update(captor.capture());
        assertThat(captor.getValue().getExecutionRecords()).hasSize(2);
        assertThat(captor.getValue().getExecutionRecords().get(1).getAction()).isEqualTo(ExecutionAction.END);
        assertThat(captor.getValue().getPointsCalculationSnapshot()).isNotNull();
        assertThat(captor.getValue().getPointsCalculationSnapshot().getTotalPoints()).isEqualByComparingTo(BigDecimal.valueOf(60));
        verify(userInventoryRepository).save(any());
        assertThat(response.getActualReward()).isEqualByComparingTo(BigDecimal.valueOf(60));
    }

    @Test
    void completeTask_已结束() {
        TaskInstanceBO instance = createEndedInstance();
        when(taskInstanceRepository.findById(INSTANCE_ID)).thenReturn(Optional.of(instance));

        assertThatThrownBy(() -> service.completeTask(INSTANCE_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("任务已结束");
        verify(pointsCalculationService, never()).calculate(any(), any(), any());
    }

    @Test
    void cancelTask_成功() {
        TaskInstanceBO instance = createRunningInstance();
        when(taskInstanceRepository.findById(INSTANCE_ID)).thenReturn(Optional.of(instance));
        when(taskInstanceRepository.update(any(TaskInstanceBO.class))).thenAnswer(i -> i.getArgument(0));
        when(pointsCalculationService.calculate(any(), any(), any()))
                .thenReturn(PointsCalculationResult.empty());

        TaskExecutionResponse response = service.cancelTask(INSTANCE_ID);

        verify(userInventoryRepository, never()).save(any());
        verify(userInventoryRepository, never()).update(any());
        assertThat(response.getStatus()).isEqualTo(TaskInstanceConstants.STATUS_CANCELLED);
        assertThat(response.getActualReward()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void getTaskExecutions_END任务无快照补全() {
        TaskInstanceBO instance = createEndedInstance();
        instance.setPointsCalculationSnapshot(null);
        when(taskInstanceRepository.listByQuery(any())).thenReturn(Collections.singletonList(instance));
        when(taskInstanceRepository.countByQuery(any())).thenReturn(1L);
        when(taskInstanceRepository.update(any(TaskInstanceBO.class))).thenAnswer(i -> i.getArgument(0));

        PointsCalculationResult calcResult = PointsCalculationResult.of(
                BigDecimal.valueOf(60), Collections.emptyList());
        when(pointsCalculationService.calculate(any(), any(), any())).thenReturn(calcResult);

        TaskExecutionQueryCommand command = new TaskExecutionQueryCommand();
        command.setPage(1);
        command.setPageSize(10);
        command.setState("all");
        service.getTaskExecutions(command);

        verify(pointsCalculationService).calculate(any(), any(), any());
        verify(taskInstanceRepository).update(any(TaskInstanceBO.class));
    }

    private TaskInstanceBO createRunningInstance() {
        TaskInstanceBO bo = TaskInstanceBO.builder()
                .id(INSTANCE_ID)
                .instanceNo("EXE001")
                .name("测试任务")
                .instanceState(InstanceState.RUNNING)
                .minUnitPoint(1)
                .startTime(LocalDateTime.now().minusMinutes(60))
                .executeById(USER_ID)
                .executeBy(USER_NO)
                .build();
        bo.appendExecutionRecord(ExecutionRecordVO.of(ExecutionAction.START, LocalDateTime.now().minusMinutes(60)));
        return bo;
    }

    private TaskInstanceBO createPausedInstance() {
        TaskInstanceBO bo = createRunningInstance();
        bo.setInstanceState(InstanceState.PAUSED);
        bo.appendExecutionRecord(ExecutionRecordVO.of(ExecutionAction.PAUSE, LocalDateTime.now().minusMinutes(30)));
        return bo;
    }

    private TaskInstanceBO createEndedInstance() {
        TaskInstanceBO bo = createRunningInstance();
        bo.setInstanceState(InstanceState.END);
        bo.setEndTime(LocalDateTime.now());
        bo.appendExecutionRecord(ExecutionRecordVO.of(ExecutionAction.END, LocalDateTime.now()));
        return bo;
    }
}
