package com.star.reward.domain.taskinstance.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 积分计算明细项
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsDetailItem {

    /** 区间类型 */
    private ExecutionIntervalType intervalType;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 结束时间 */
    private LocalDateTime endTime;

    /** 持续分钟数 */
    private Long durationMinutes;

    /** 积分倍数 */
    private Integer multiplier;

    /** 所得积分 */
    private BigDecimal points;
}
