package com.star.reward.domain.userinventory.model.entity;

import com.star.reward.domain.userinventory.model.constant.UserInventoryConstants;
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

    /**
     * 工厂方法：创建积分库存
     *
     * @param inventoryNo 库存编号
     * @param userNo 所属人账号
     * @param userId 所属人ID
     * @param quantity 初始数量
     * @param now 创建时间
     */
    public static UserInventoryBO createPointInventory(String inventoryNo, String userNo, Long userId,
            BigDecimal quantity, LocalDateTime now) {
        return UserInventoryBO.builder()
                .inventoryNo(inventoryNo)
                .inventoryType(InventoryType.POINT)
                .name(UserInventoryConstants.POINT_INVENTORY_NAME)
                .description(UserInventoryConstants.POINT_INVENTORY_DESCRIPTION)
                .quantity(quantity)
                .unit(UserInventoryConstants.POINT_UNIT)
                .belongTo(userNo)
                .belongToId(userId)
                .isDeleted(false)
                .createBy(userNo)
                .createById(userId)
                .createTime(now)
                .build();
    }

    /**
     * 工厂方法：创建商品库存
     *
     * @param inventoryNo 库存编号
     * @param name 商品名称
     * @param description 商品描述
     * @param quantity 数量
     * @param unit 单位
     * @param publishBy 发布人账号
     * @param publishById 发布人ID
     * @param belongTo 所属人账号
     * @param belongToId 所属人ID
     * @param now 创建时间
     */
    public static UserInventoryBO createProductInventory(String inventoryNo, String name, String description,
            BigDecimal quantity, String unit, String publishBy, Long publishById, String belongTo, Long belongToId,
            LocalDateTime now) {
        return UserInventoryBO.builder()
                .inventoryNo(inventoryNo)
                .inventoryType(InventoryType.PRODUCT)
                .name(name)
                .description(description)
                .quantity(quantity)
                .unit(unit)
                .publishBy(publishBy)
                .publishById(publishById)
                .belongTo(belongTo)
                .belongToId(belongToId)
                .isDeleted(false)
                .createBy(belongTo)
                .createById(belongToId)
                .createTime(now)
                .build();
    }
}
