package com.star.reward.application.assembler;

import com.star.reward.domain.purchaserecord.service.ExchangeService;
import com.star.reward.interfaces.rest.dto.response.ExchangeResponse;

/**
 * 购买记录转换器（Application → 输出边界）
 * 领域结果 → ExchangeResponse
 */
public final class PurchaseRecordAssembler {

    private PurchaseRecordAssembler() {
    }

    /**
     * ExchangeResult → ExchangeResponse
     */
    public static ExchangeResponse toExchangeResponse(ExchangeService.ExchangeResult result) {
        if (result == null) {
            return null;
        }
        return ExchangeResponse.builder()
                .product(ProductAssembler.entityToResponse(result.getProduct()))
                .quantity(result.getQuantity())
                .pointsSpent(result.getPointsSpent())
                .remainingPoints(result.getRemainingPoints())
                .build();
    }
}
