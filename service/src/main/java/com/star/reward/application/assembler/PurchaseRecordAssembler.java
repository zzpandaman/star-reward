package com.star.reward.application.assembler;

import com.star.reward.domain.purchaserecord.model.entity.PurchaseRecordBO;
import com.star.reward.domain.purchaserecord.service.ExchangeService;
import com.star.reward.interfaces.rest.dto.response.ExchangeResponse;
import com.star.reward.interfaces.rest.dto.response.PurchaseRecordResponse;

/**
 * 购买记录转换器（Application → 输出边界）
 * 领域结果 → ExchangeResponse、Entity → PurchaseRecordResponse
 */
public final class PurchaseRecordAssembler {

    private PurchaseRecordAssembler() {
    }

    /**
     * Entity → PurchaseRecordResponse
     */
    public static PurchaseRecordResponse entityToResponse(PurchaseRecordBO bo) {
        if (bo == null) {
            return null;
        }
        return PurchaseRecordResponse.builder()
                .id(bo.getId())
                .purchaseNo(bo.getPurchaseNo())
                .productNo(bo.getProductNo())
                .name(bo.getName())
                .description(bo.getDescription())
                .price(bo.getPrice())
                .minQuantity(bo.getMinQuantity())
                .minUnit(bo.getMinUnit())
                .purchaseQuantity(bo.getPurchaseQuantity())
                .createTime(bo.getCreateTime())
                .build();
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
