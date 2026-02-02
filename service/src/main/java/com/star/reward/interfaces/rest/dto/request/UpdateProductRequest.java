package com.star.reward.interfaces.rest.dto.request;

import lombok.Data;

import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * 更新商品请求
 */
@Data
public class UpdateProductRequest {
    
    /**
     * 商品名称
     */
    private String name;
    
    /**
     * 商品描述
     */
    private String description;
    
    /**
     * 单价（积分）
     */
    @Positive(message = "单价必须大于0")
    private BigDecimal price;
    
    /**
     * 最小购买数量
     */
    @Positive(message = "最小购买数量必须大于0")
    private BigDecimal minQuantity;
    
    /**
     * 单位
     */
    private String unit;
}
