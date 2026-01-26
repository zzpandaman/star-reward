package com.star.reward.api;

import com.star.reward.api.dto.request.RewardQueryRequest;
import com.star.reward.api.dto.response.RewardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 奖励服务 Feign API 接口
 * 提供给其他服务调用的接口定义
 */
@FeignClient(name = "reward-service", path = "/api/reward")
public interface RewardFeignApi {

    /**
     * 查询奖励信息
     */
    @GetMapping("/query")
    RewardResponse queryReward(@RequestParam("id") Long id);

    /**
     * 创建奖励
     */
    @PostMapping("/create")
    RewardResponse createReward(@RequestBody RewardQueryRequest request);
}
