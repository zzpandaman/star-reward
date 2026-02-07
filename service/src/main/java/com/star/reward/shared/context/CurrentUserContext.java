package com.star.reward.shared.context;

import lombok.Data;

import java.util.List;

/**
 * 当前用户上下文
 */
@Data
public class CurrentUserContext {
    
    /**
     * 用户编号
     */
    private String userNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 角色列表
     */
    private List<String> roles;
    
    /**
     * 权限列表
     */
    private List<String> permissions;
    
    private static final ThreadLocal<CurrentUserContext> CONTEXT = new ThreadLocal<>();
    
    /**
     * 设置当前用户
     */
    public static void set(CurrentUserContext user) {
        CONTEXT.set(user);
    }
    
    /**
     * 获取当前用户
     */
    public static CurrentUserContext get() {
        return CONTEXT.get();
    }
    
    /**
     * 清除当前用户
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
