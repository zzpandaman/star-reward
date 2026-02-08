package com.star.reward.shared.interceptor;

import com.star.common.result.Result;
import com.star.common.result.ResultCode;
import com.star.reward.shared.context.CurrentUserContext;
import com.alibaba.fastjson2.JSON;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * JWT认证拦截器
 */
@Slf4j
@Component
public class JwtAuthInterceptor implements HandlerInterceptor {
    
    @Value("${jwt.secret:star-sso-jwt-secret-key-change-in-production}")
    private String jwtSecret;
    
    @Value("${jwt.header:Authorization}")
    private String jwtHeader;
    
    @Value("${jwt.prefix:Bearer }")
    private String jwtPrefix;
    
    /**
     * 获取密钥
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 排除登录接口等不需要认证的接口
        String uri = request.getRequestURI();
        if (uri.startsWith("/api/sso/login")
                || uri.startsWith("/actuator")
                || uri.startsWith("/api/reward/import")
        ) {
            return true;
        }
        
        // 获取Token
        String authHeader = request.getHeader(jwtHeader);
        if (authHeader == null || !authHeader.startsWith(jwtPrefix)) {
            writeErrorResponse(response, ResultCode.UNAUTHORIZED);
            return false;
        }
        
        String token = authHeader.substring(jwtPrefix.length());
        
        try {
            // 解析Token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            // 检查Token是否过期
            if (claims.getExpiration().before(new java.util.Date())) {
                writeErrorResponse(response, ResultCode.TOKEN_EXPIRED);
                return false;
            }
            
            // 设置当前用户上下文
            CurrentUserContext userContext = new CurrentUserContext();
            userContext.setUserNo(claims.getSubject());
            userContext.setUserId(claims.get("userId", Long.class));
            userContext.setUsername(claims.get("username", String.class));
            
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.get("roles");
            userContext.setRoles(roles);
            
            @SuppressWarnings("unchecked")
            List<String> permissions = (List<String>) claims.get("permissions");
            userContext.setPermissions(permissions);
            
            CurrentUserContext.set(userContext);
            
            return true;
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            writeErrorResponse(response, ResultCode.TOKEN_INVALID);
            return false;
        }
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除ThreadLocal，防止内存泄漏
        CurrentUserContext.clear();
    }
    
    /**
     * 写入错误响应
     */
    private void writeErrorResponse(HttpServletResponse response, ResultCode resultCode) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Result<?> result = Result.fail(resultCode.getCode(), resultCode.getMessage());
        response.getWriter().write(JSON.toJSONString(result));
    }
}
