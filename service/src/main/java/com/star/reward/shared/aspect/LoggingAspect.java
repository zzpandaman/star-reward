package com.star.reward.shared.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 日志切面
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("@annotation(com.star.reward.shared.annotation.Log)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getName();
        
        log.info("方法执行开始: {}.{}", className, methodName);
        
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            log.info("方法执行结束: {}.{}, 耗时: {}ms", className, methodName, duration);
            return result;
        } catch (Throwable e) {
            log.error("方法执行异常: {}.{}", className, methodName, e);
            throw e;
        }
    }
}
