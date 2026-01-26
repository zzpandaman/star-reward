package com.star.reward.domain.userinventory.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 库存类型值对象
 */
@Getter
@AllArgsConstructor
public enum InventoryType {
    
    /**
     * 积分
     */
    POINT("POINT", "积分"),
    
    /**
     * 商品
     */
    PRODUCT("PRODUCT", "商品");
    
    private final String code;
    private final String description;
    
    /**
     * 根据代码获取枚举
     */
    public static InventoryType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (InventoryType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown InventoryType code: " + code);
    }
}
