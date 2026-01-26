package com.star.reward.infrastructure.external.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 示例外部服务客户端
 * 用于调用其他服务的 Feign 客户端
 */
@FeignClient(name = "example-service", path = "/api/example", fallback = ExampleServiceClientFallback.class)
public interface ExampleServiceClient {

    @GetMapping("/query")
    String query(@RequestParam("id") Long id);
}
