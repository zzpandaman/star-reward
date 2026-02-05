package com.star.reward.domain.taskinstance.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 积分计算快照（存储到扩展字段，支持查询）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsCalculationSnapshot {

    /** 总积分 */
    private BigDecimal totalPoints;

    /** 每分钟基础积分 */
    private Integer pointsPerMinute;

    /** 计算时间 */
    private LocalDateTime calculatedAt;

    /** 明细列表 */
    private List<PointsDetailItem> details;
}
