package com.star.reward.interfaces.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 兑换响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeResponse {
    
    /**
     * 兑换的商品
     */
    private ProductResponse product;
    
    /**
     * 兑换数量
     */
    private BigDecimal quantity;
    
    /**
     * 消耗积分
     */
    private BigDecimal pointsSpent;
    
    /**
     * 剩余积分
     */
    private BigDecimal remainingPoints;
}
