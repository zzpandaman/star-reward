package com.star.reward.interfaces.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    
    /**
     * ID
     */
    private Long id;
    
    /**
     * 商品编号
     */
    private String productNo;
    
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
    private BigDecimal price;
    
    /**
     * 最小购买数量
     */
    private BigDecimal minQuantity;
    
    /**
     * 单位
     */
    private String unit;
    
    /**
     * 是否为预设商品
     */
    private Boolean isPreset;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
