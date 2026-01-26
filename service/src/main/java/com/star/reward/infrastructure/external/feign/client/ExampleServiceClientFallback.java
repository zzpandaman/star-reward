package com.star.reward.infrastructure.external.feign.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 示例服务降级处理
 */
@Slf4j
@Component
public class ExampleServiceClientFallback implements ExampleServiceClient {

    @Override
    public String query(Long id) {
        log.warn("ExampleServiceClient 降级处理, id: {}", id);
        return "降级响应";
    }
}
