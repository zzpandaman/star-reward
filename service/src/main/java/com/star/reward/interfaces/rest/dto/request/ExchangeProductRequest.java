package com.star.reward.interfaces.rest.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * 兑换商品请求
 */
@Data
public class ExchangeProductRequest {
    
    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    /**
     * 兑换数量
     */
    @Positive(message = "兑换数量必须大于0")
    private BigDecimal quantity;
}
