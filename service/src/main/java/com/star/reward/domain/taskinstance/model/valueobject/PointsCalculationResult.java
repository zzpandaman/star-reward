package com.star.reward.domain.taskinstance.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Collections;
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

    /**
     * 空结果
     */
    public static PointsCalculationResult empty() {
        return PointsCalculationResult.builder()
                .totalPoints(BigDecimal.ZERO)
                .details(Collections.emptyList())
                .build();
    }

    /**
     * 工厂方法：创建带明细的结果
     */
    public static PointsCalculationResult of(BigDecimal totalPoints, List<PointsDetailItem> details) {
        return PointsCalculationResult.builder()
                .totalPoints(totalPoints != null ? totalPoints : BigDecimal.ZERO)
                .details(details != null ? details : Collections.emptyList())
                .build();
    }
}
