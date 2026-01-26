package com.star.reward.interfaces.rest.controller;

import com.star.reward.shared.context.CurrentUserContext;
import com.star.reward.shared.result.Result;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 当前用户控制器
 */
@RestController
@RequestMapping("/api")
public class CurrentUserController {
    
    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current-user")
    public Result<CurrentUserInfo> getCurrentUser() {
        CurrentUserContext context = CurrentUserContext.get();
        if (context == null) {
            return Result.fail(com.star.reward.shared.result.ResultCode.UNAUTHORIZED.getCode(), 
                    com.star.reward.shared.result.ResultCode.UNAUTHORIZED.getMessage());
        }
        
        CurrentUserInfo userInfo = CurrentUserInfo.builder()
                .userNo(context.getUserNo())
                .userId(context.getUserId())
                .username(context.getUsername())
                .roles(context.getRoles())
                .permissions(context.getPermissions())
                .build();
        
        return Result.success(userInfo);
    }
    
    /**
     * 当前用户信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentUserInfo {
        private String userNo;
        private Long userId;
        private String username;
        private List<String> roles;
        private List<String> permissions;
    }
}
