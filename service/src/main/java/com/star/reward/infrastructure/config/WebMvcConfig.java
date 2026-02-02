package com.star.reward.infrastructure.config;

import com.star.reward.shared.interceptor.JwtAuthInterceptor;
import com.star.reward.shared.interceptor.LoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    
    private final JwtAuthInterceptor jwtAuthInterceptor;
    private final LoggingInterceptor loggingInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 日志拦截器
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**");
        
        // JWT认证拦截器
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/sso/login",
                        "/api/reward/health",
                        "/actuator/**",
                        "/error"
                );
    }
}
