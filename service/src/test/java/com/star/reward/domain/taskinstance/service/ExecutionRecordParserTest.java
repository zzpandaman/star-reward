package com.star.reward.domain.taskinstance.service;

import com.star.reward.domain.taskinstance.model.valueobject.ExecutionAction;
import com.star.reward.domain.taskinstance.model.valueobject.ExecutionInterval;
import com.star.reward.domain.taskinstance.model.valueobject.ExecutionIntervalType;
import com.star.reward.domain.taskinstance.model.valueobject.ExecutionRecordVO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ExecutionRecordParser 单元测试
 */
class ExecutionRecordParserTest {

    private static final LocalDateTime T10 = LocalDateTime.of(2025, 2, 5, 10, 0);
    private static final LocalDateTime T11 = LocalDateTime.of(2025, 2, 5, 11, 0);
    private static final LocalDateTime T12 = LocalDateTime.of(2025, 2, 5, 12, 0);
    private static final LocalDateTime T13 = LocalDateTime.of(2025, 2, 5, 13, 0);

    private static ExecutionRecordVO record(ExecutionAction action, LocalDateTime time) {
        return ExecutionRecordVO.builder().action(action).actionTime(time).build();
    }

    @Test
    void toExecutionIntervals_空或单条() {
        assertThat(ExecutionRecordParser.toExecutionIntervals(null)).isEmpty();
        assertThat(ExecutionRecordParser.toExecutionIntervals(Collections.emptyList())).isEmpty();
        assertThat(ExecutionRecordParser.toExecutionIntervals(
                Collections.singletonList(record(ExecutionAction.START, T10)))).isEmpty();
    }

    @Test
    void toExecutionIntervals_无暂停直接结束() {
        List<ExecutionRecordVO> records = Arrays.asList(
                record(ExecutionAction.START, T10),
                record(ExecutionAction.END, T11));
        List<ExecutionInterval> result = ExecutionRecordParser.toExecutionIntervals(records);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIntervalType()).isEqualTo(ExecutionIntervalType.START_END);
        assertThat(result.get(0).getRange().durationMinutes()).isEqualTo(60L);
    }

    @Test
    void toExecutionIntervals_有暂停恢复() {
        List<ExecutionRecordVO> records = Arrays.asList(
                record(ExecutionAction.START, T10),
                record(ExecutionAction.PAUSE, T11),
                record(ExecutionAction.RESUME, T12),
                record(ExecutionAction.END, T13));
        List<ExecutionInterval> result = ExecutionRecordParser.toExecutionIntervals(records);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getIntervalType()).isEqualTo(ExecutionIntervalType.START_PAUSE);
        assertThat(result.get(0).getRange().durationMinutes()).isEqualTo(60L);
        assertThat(result.get(1).getIntervalType()).isEqualTo(ExecutionIntervalType.RESUME_END);
        assertThat(result.get(1).getRange().durationMinutes()).isEqualTo(60L);
    }

    @Test
    void toExecutionIntervals_多次暂停() {
        List<ExecutionRecordVO> records = Arrays.asList(
                record(ExecutionAction.START, T10),
                record(ExecutionAction.PAUSE, T11),
                record(ExecutionAction.RESUME, T11.plusMinutes(5)),
                record(ExecutionAction.PAUSE, T12),
                record(ExecutionAction.RESUME, T12.plusMinutes(5)),
                record(ExecutionAction.END, T13));
        List<ExecutionInterval> result = ExecutionRecordParser.toExecutionIntervals(records);
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getIntervalType()).isEqualTo(ExecutionIntervalType.START_PAUSE);
        assertThat(result.get(1).getIntervalType()).isEqualTo(ExecutionIntervalType.RESUME_PAUSE);
        assertThat(result.get(2).getIntervalType()).isEqualTo(ExecutionIntervalType.RESUME_END);
    }

    @Test
    void toExecutionIntervals_跳过非计分() {
        List<ExecutionRecordVO> records = Arrays.asList(
                record(ExecutionAction.START, T10),
                record(ExecutionAction.PAUSE, T11),
                record(ExecutionAction.RESUME, T12),
                record(ExecutionAction.END, T13));
        List<ExecutionInterval> result = ExecutionRecordParser.toExecutionIntervals(records);
        assertThat(result).noneMatch(i -> i.getIntervalType() == ExecutionIntervalType.PAUSE_RESUME);
    }

    @Test
    void toExecutionIntervals_含null跳过() {
        ExecutionRecordVO withNullAction = ExecutionRecordVO.builder()
                .action(null)
                .actionTime(T10)
                .build();
        List<ExecutionRecordVO> records = Arrays.asList(
                record(ExecutionAction.START, T10),
                withNullAction,
                record(ExecutionAction.END, T11));
        List<ExecutionInterval> result = ExecutionRecordParser.toExecutionIntervals(records);
        assertThat(result).isEmpty();
    }

    @Test
    void toExecutionIntervals_actionTime为null跳过() {
        ExecutionRecordVO withNullActionTime = ExecutionRecordVO.builder()
                .action(ExecutionAction.PAUSE)
                .actionTime(null)
                .build();
        List<ExecutionRecordVO> records = Arrays.asList(
                record(ExecutionAction.START, T10),
                withNullActionTime,
                record(ExecutionAction.RESUME, T11));
        List<ExecutionInterval> result = ExecutionRecordParser.toExecutionIntervals(records);
        assertThat(result).isEmpty();
    }
}
