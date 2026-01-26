package com.star.reward.domain.tasktemplate.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 最小单位值对象
 */
@Getter
@AllArgsConstructor
public enum MinUnit {
    
    /**
     * 分钟
     */
    MIN("MIN", "分钟");
    
    private final String code;
    private final String description;
    
    /**
     * 根据代码获取枚举
     */
    public static MinUnit fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (MinUnit unit : values()) {
            if (unit.getCode().equals(code)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Unknown MinUnit code: " + code);
    }
}
