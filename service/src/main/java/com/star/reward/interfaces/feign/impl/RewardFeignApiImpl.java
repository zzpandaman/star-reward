package com.star.reward.interfaces.feign.impl;

import com.star.reward.api.RewardFeignApi;
import com.star.reward.api.dto.request.RewardQueryRequest;
import com.star.reward.api.dto.response.RewardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * 奖励 Feign API 实现
 * 实现 api-client 模块中的 RewardFeignApi 接口
 */
@RestController
@RequiredArgsConstructor
public class RewardFeignApiImpl implements RewardFeignApi {

    // TODO: 注入应用服务
    // private final RewardApplicationService rewardApplicationService;

    @Override
    public RewardResponse queryReward(Long id) {
        // TODO: 调用应用服务
        RewardResponse response = new RewardResponse();
        response.setId(id);
        return response;
    }

    @Override
    public RewardResponse createReward(RewardQueryRequest request) {
        // TODO: 调用应用服务
        RewardResponse response = new RewardResponse();
        response.setId(1L);
        return response;
    }
}
