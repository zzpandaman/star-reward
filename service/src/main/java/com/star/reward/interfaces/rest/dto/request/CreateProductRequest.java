package com.star.reward.interfaces.rest.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * 创建商品请求
 */
@Data
public class CreateProductRequest {
    
    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String name;
    
    /**
     * 商品描述
     */
    @NotBlank(message = "商品描述不能为空")
    private String description;
    
    /**
     * 单价（积分）
     */
    @NotNull(message = "单价不能为空")
    @Positive(message = "单价必须大于0")
    private BigDecimal price;
    
    /**
     * 最小购买数量
     */
    @NotNull(message = "最小购买数量不能为空")
    @Positive(message = "最小购买数量必须大于0")
    private BigDecimal minQuantity;
    
    /**
     * 单位
     */
    private String unit;
}
