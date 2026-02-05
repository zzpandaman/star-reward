package com.star.reward.domain.taskinstance.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 积分转换对象（倍率卡有效时段）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsConversionSegment {

    /** 积分倍数 */
    private Integer multiplier;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 持续时间（分钟） */
    private Long durationMinutes;

    /**
     * 转为 TimeRange 便于与执行区间求交
     */
    public TimeRange toTimeRange() {
        if (startTime == null || durationMinutes == null || durationMinutes <= 0) {
            return null;
        }
        return TimeRange.of(startTime, durationMinutes);
    }
}
