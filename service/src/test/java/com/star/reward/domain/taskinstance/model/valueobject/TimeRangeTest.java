package com.star.reward.domain.taskinstance.model.valueobject;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TimeRange 单元测试
 */
class TimeRangeTest {

    private static final LocalDateTime T10 = LocalDateTime.of(2025, 2, 5, 10, 0);
    private static final LocalDateTime T11 = LocalDateTime.of(2025, 2, 5, 11, 0);
    private static final LocalDateTime T12 = LocalDateTime.of(2025, 2, 5, 12, 0);
    private static final LocalDateTime T13 = LocalDateTime.of(2025, 2, 5, 13, 0);

    @Test
    void intersect_有交集() {
        TimeRange a = TimeRange.of(T10, T12);
        TimeRange b = TimeRange.of(T11, T13);
        TimeRange result = a.intersect(b);
        assertThat(result).isNotNull();
        assertThat(result.getStart()).isEqualTo(T11);
        assertThat(result.getEnd()).isEqualTo(T12);
    }

    @Test
    void intersect_无交集() {
        TimeRange a = TimeRange.of(T10, T11);
        TimeRange b = TimeRange.of(T12, T13);
        assertThat(a.intersect(b)).isNull();
        assertThat(b.intersect(a)).isNull();
    }

    @Test
    void intersect_边界相接() {
        TimeRange a = TimeRange.of(T10, T11);
        TimeRange b = TimeRange.of(T11, T12);
        assertThat(a.intersect(b)).isNull();
    }

    @Test
    void intersect_包含关系() {
        TimeRange outer = TimeRange.of(T10, T13);
        TimeRange inner = TimeRange.of(T11, T12);
        TimeRange result = outer.intersect(inner);
        assertThat(result).isNotNull();
        assertThat(result.getStart()).isEqualTo(T11);
        assertThat(result.getEnd()).isEqualTo(T12);
    }

    @Test
    void intersect_other为null() {
        TimeRange a = TimeRange.of(T10, T11);
        assertThat(a.intersect(null)).isNull();
    }

    @Test
    void durationMinutes() {
        TimeRange range = TimeRange.of(T10, T12);
        assertThat(range.durationMinutes()).isEqualTo(120L);
    }

    @Test
    void of_startAndDuration() {
        TimeRange range = TimeRange.of(T10, 30);
        assertThat(range.getStart()).isEqualTo(T10);
        assertThat(range.getEnd()).isEqualTo(T10.plusMinutes(30));
        assertThat(range.durationMinutes()).isEqualTo(30L);
    }
}
