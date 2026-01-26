package com.star.reward.domain.taskinstance.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 实例状态值对象
 */
@Getter
@AllArgsConstructor
public enum InstanceState {
    
    /**
     * 执行中
     */
    RUNNING("RUNNING", "执行中"),
    
    /**
     * 暂停
     */
    PAUSED("PAUSED", "暂停"),
    
    /**
     * 结束
     */
    END("END", "结束");
    
    private final String code;
    private final String description;
    
    /**
     * 根据代码获取枚举
     */
    public static InstanceState fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (InstanceState state : values()) {
            if (state.getCode().equals(code)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown InstanceState code: " + code);
    }
}
