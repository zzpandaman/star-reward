package com.star.reward.domain.purchaserecord.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购买记录领域实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRecordBO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 购买记录编号
     */
    private String purchaseNo;
    
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
     * 单价
     */
    private BigDecimal price;
    
    /**
     * 最小购买数量
     */
    private BigDecimal minQuantity;
    
    /**
     * 单位（g、min等）
     */
    private String minUnit;
    
    /**
     * 发布人账号
     */
    private String publishBy;
    
    /**
     * 发布人ID
     */
    private Long publishById;
    
    /**
     * 购买份数
     */
    private Integer purchaseQuantity;
    
    /**
     * 购买人账号
     */
    private String purchaseBy;
    
    /**
     * 购买人ID
     */
    private Long purchaseById;
    
    /**
     * 是否为预设。0-否 1-是
     */
    private Boolean isPreset;
    
    /**
     * 是否删除。0-否 1-是
     */
    private Boolean isDeleted;
    
    /**
     * 创建人账号
     */
    private String createBy;
    
    /**
     * 创建人ID
     */
    private Long createById;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新人账号
     */
    private String updateBy;
    
    /**
     * 更新人ID
     */
    private Long updateById;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 扩展字段（JSON格式）
     */
    private String attributes;
}
