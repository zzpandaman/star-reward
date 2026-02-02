package com.star.reward.interfaces.rest.controller;

import com.star.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查控制器
 */
@RestController
@RequestMapping("/api/reward")
public class HealthController {

    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("OK");
    }
}
