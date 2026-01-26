package com.star.reward.domain.userinventory.model.entity;

import com.star.reward.domain.userinventory.model.valueobject.InventoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户库存领域实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInventoryBO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 库存编号
     */
    private String inventoryNo;
    
    /**
     * 库存类型
     */
    private InventoryType inventoryType;
    
    /**
     * 库存名称
     */
    private String name;
    
    /**
     * 库存描述
     */
    private String description;
    
    /**
     * 库存数量
     */
    private BigDecimal quantity;
    
    /**
     * 单位（g、min等）
     */
    private String unit;
    
    /**
     * 发布人账号
     */
    private String publishBy;
    
    /**
     * 发布人ID
     */
    private Long publishById;
    
    /**
     * 所属人账号
     */
    private String belongTo;
    
    /**
     * 所属人ID
     */
    private Long belongToId;
    
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
