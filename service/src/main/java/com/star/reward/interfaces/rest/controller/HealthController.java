package com.star.reward.interfaces.rest.controller;

import com.star.common.result.Result;
import com.star.reward.interfaces.rest.dto.request.EmptyRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查控制器
 */
@RestController
@RequestMapping("/api/reward")
public class HealthController {

    @PostMapping("/health")
    public Result<String> health(@Validated @RequestBody EmptyRequest request) {
        return Result.success("OK");
    }
}
