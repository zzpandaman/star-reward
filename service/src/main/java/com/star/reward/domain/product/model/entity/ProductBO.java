package com.star.reward.domain.product.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品领域实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBO {
    
    /**
     * 主键ID
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

    /**
     * 充血方法：初始化创建时的编号、默认值、审计字段
     *
     * @param productNo 商品编号
     * @param userNo 用户账号
     * @param userId 用户ID
     * @param now 创建时间
     */
    public void initForCreate(String productNo, String userNo, Long userId, LocalDateTime now) {
        this.productNo = productNo;
        this.isPreset = false;
        this.isDeleted = false;
        this.publishBy = userNo;
        this.publishById = userId;
        this.createBy = userNo;
        this.createById = userId;
        this.createTime = now;
        this.updateBy = userNo;
        this.updateById = userId;
        this.updateTime = now;
    }
}
