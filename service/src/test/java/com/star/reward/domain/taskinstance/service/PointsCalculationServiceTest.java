package com.star.reward.domain.taskinstance.service;

import com.star.reward.domain.taskinstance.model.valueobject.ExecutionInterval;
import com.star.reward.domain.taskinstance.model.valueobject.ExecutionIntervalType;
import com.star.reward.domain.taskinstance.model.valueobject.PointsConversionSegment;
import com.star.reward.domain.taskinstance.model.valueobject.PointsDetailItem;
import com.star.reward.domain.taskinstance.model.valueobject.PointsCalculationResult;
import com.star.reward.domain.taskinstance.model.valueobject.TimeRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PointsCalculationService 单元测试
 */
class PointsCalculationServiceTest {

    private static final LocalDateTime T10 = LocalDateTime.of(2025, 2, 5, 10, 0);
    private static final LocalDateTime T11 = LocalDateTime.of(2025, 2, 5, 11, 0);
    private static final LocalDateTime T12 = LocalDateTime.of(2025, 2, 5, 12, 0);
    private static final LocalDateTime T13 = LocalDateTime.of(2025, 2, 5, 13, 0);

    private PointsCalculationService service;

    @BeforeEach
    void setUp() {
        service = new PointsCalculationService();
    }

    @Test
    void calculate_空区间() {
        PointsCalculationResult result = service.calculate(
                Collections.emptyList(), Collections.emptyMap(), 1);
        assertThat(result.getTotalPoints()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getDetails()).isEmpty();
    }

    @Test
    void calculate_无倍率卡() {
        TimeRange range = TimeRange.of(T10, T12);
        List<ExecutionInterval> intervals = Collections.singletonList(
                ExecutionInterval.of(range, ExecutionIntervalType.START_END));
        PointsCalculationResult result = service.calculate(
                intervals, Collections.emptyMap(), 1);
        assertThat(result.getTotalPoints()).isEqualByComparingTo(BigDecimal.valueOf(120));
        assertThat(result.getDetails()).hasSize(1);
        assertThat(result.getDetails().get(0).getMultiplier()).isEqualTo(1);
    }

    @Test
    void calculate_单倍率卡覆盖() {
        TimeRange execRange = TimeRange.of(T10, T12);
        PointsConversionSegment card = PointsConversionSegment.builder()
                .multiplier(2)
                .startTime(T10)
                .durationMinutes(120L)
                .build();
        Map<Integer, List<PointsConversionSegment>> multiplierToSegments =
                Collections.singletonMap(2, Collections.singletonList(card));
        List<ExecutionInterval> intervals = Collections.singletonList(
                ExecutionInterval.of(execRange, ExecutionIntervalType.START_END));
        PointsCalculationResult result = service.calculate(
                intervals, multiplierToSegments, 1);
        assertThat(result.getTotalPoints()).isEqualByComparingTo(BigDecimal.valueOf(240));
        assertThat(result.getDetails()).hasSize(1);
        assertThat(result.getDetails().get(0).getMultiplier()).isEqualTo(2);
    }

    @Test
    void calculate_倍率卡部分覆盖() {
        TimeRange execRange = TimeRange.of(T10, T13);
        PointsConversionSegment card = PointsConversionSegment.builder()
                .multiplier(2)
                .startTime(T11)
                .durationMinutes(60L)
                .build();
        Map<Integer, List<PointsConversionSegment>> multiplierToSegments =
                Collections.singletonMap(2, Collections.singletonList(card));
        List<ExecutionInterval> intervals = Collections.singletonList(
                ExecutionInterval.of(execRange, ExecutionIntervalType.START_END));
        PointsCalculationResult result = service.calculate(
                intervals, multiplierToSegments, 1);
        assertThat(result.getTotalPoints()).isEqualByComparingTo(BigDecimal.valueOf(240));
        assertThat(result.getDetails()).hasSize(3);
        assertThat(result.getDetails().stream()
                .mapToLong(PointsDetailItem::getDurationMinutes)
                .sum()).isEqualTo(180L);
    }

    @Test
    void calculate_多倍率卡重叠取max() {
        TimeRange execRange = TimeRange.of(T10, T12);
        PointsConversionSegment card2x = PointsConversionSegment.builder()
                .multiplier(2)
                .startTime(T10)
                .durationMinutes(120L)
                .build();
        PointsConversionSegment card3x = PointsConversionSegment.builder()
                .multiplier(3)
                .startTime(T10)
                .durationMinutes(120L)
                .build();
        Map<Integer, List<PointsConversionSegment>> multiplierToSegments = new HashMap<>();
        multiplierToSegments.put(2, Collections.singletonList(card2x));
        multiplierToSegments.put(3, Collections.singletonList(card3x));
        List<ExecutionInterval> intervals = Collections.singletonList(
                ExecutionInterval.of(execRange, ExecutionIntervalType.START_END));
        PointsCalculationResult result = service.calculate(
                intervals, multiplierToSegments, 1);
        assertThat(result.getTotalPoints()).isEqualByComparingTo(BigDecimal.valueOf(360));
        assertThat(result.getDetails()).hasSize(1);
        assertThat(result.getDetails().get(0).getMultiplier()).isEqualTo(3);
    }

    @Test
    void calculate_pointsPerMinute缺省() {
        TimeRange range = TimeRange.of(T10, T11);
        List<ExecutionInterval> intervals = Collections.singletonList(
                ExecutionInterval.of(range, ExecutionIntervalType.START_END));
        PointsCalculationResult result = service.calculate(
                intervals, Collections.emptyMap(), null);
        assertThat(result.getTotalPoints()).isEqualByComparingTo(BigDecimal.valueOf(60));
    }

    @Test
    void calculate_明细结构() {
        TimeRange range = TimeRange.of(T10, T11);
        List<ExecutionInterval> intervals = Collections.singletonList(
                ExecutionInterval.of(range, ExecutionIntervalType.START_END));
        PointsCalculationResult result = service.calculate(
                intervals, Collections.emptyMap(), 2);
        assertThat(result.getDetails()).hasSize(1);
        PointsDetailItem item = result.getDetails().get(0);
        assertThat(item.getIntervalType()).isEqualTo(ExecutionIntervalType.START_END);
        assertThat(item.getStartTime()).isEqualTo(T10);
        assertThat(item.getEndTime()).isEqualTo(T11);
        assertThat(item.getDurationMinutes()).isEqualTo(60L);
        assertThat(item.getMultiplier()).isEqualTo(1);
        assertThat(item.getPoints()).isEqualByComparingTo(BigDecimal.valueOf(120));
    }
}
