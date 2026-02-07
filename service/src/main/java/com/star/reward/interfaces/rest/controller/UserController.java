package com.star.reward.interfaces.rest.controller;

import com.star.common.page.PageResponse;
import com.star.common.result.Result;
import com.star.reward.application.service.UserApplicationService;
import com.star.reward.interfaces.rest.dto.response.InventoryResponse;
import com.star.reward.interfaces.rest.dto.response.UserPointsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/reward/user")
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService userApplicationService;

    /**
     * 获取用户积分
     */
    @GetMapping("/points")
    public Result<UserPointsResponse> getUserPoints() {
        UserPointsResponse response = userApplicationService.getUserPoints();
        return Result.success(response);
    }

    /**
     * 获取用户背包
     */
    @GetMapping("/inventory")
    public Result<PageResponse<InventoryResponse>> getUserInventory() {
        PageResponse<InventoryResponse> response = userApplicationService.getUserInventory();
        return Result.success(response);
    }
}
