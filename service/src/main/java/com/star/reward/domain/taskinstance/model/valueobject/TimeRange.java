package com.star.reward.domain.taskinstance.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 时间区间值对象（不可变）
 */
@Getter
@AllArgsConstructor
public class TimeRange {

    private final LocalDateTime start;
    private final LocalDateTime end;

    /**
     * 创建时间区间
     */
    public static TimeRange of(LocalDateTime start, LocalDateTime end) {
        return new TimeRange(start, end);
    }

    /**
     * 创建时间区间（开始时间 + 持续分钟数）
     */
    public static TimeRange of(LocalDateTime start, long durationMinutes) {
        return new TimeRange(start, start.plusMinutes(durationMinutes));
    }

    /**
     * 求交集，无交集返回 null
     */
    public TimeRange intersect(TimeRange other) {
        if (other == null) {
            return null;
        }
        LocalDateTime intersectStart = start.isAfter(other.start) ? start : other.start;
        LocalDateTime intersectEnd = end.isBefore(other.end) ? end : other.end;
        if (!intersectStart.isBefore(intersectEnd)) {
            return null;
        }
        return new TimeRange(intersectStart, intersectEnd);
    }

    /**
     * 持续时长（分钟）
     */
    public long durationMinutes() {
        return Duration.between(start, end).toMinutes();
    }
}
