package com.star.reward.domain.taskinstance.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 积分计算结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsCalculationResult {

    /** 总积分 */
    private BigDecimal totalPoints;

    /** 明细列表 */
    private List<PointsDetailItem> details;
}
