package com.star.reward.interfaces.rest.controller;

import com.star.common.page.PageResponse;
import com.star.common.result.Result;
import com.star.reward.application.service.UserApplicationService;
import com.star.reward.interfaces.rest.dto.request.EmptyRequest;
import com.star.reward.interfaces.rest.dto.response.InventoryResponse;
import com.star.reward.interfaces.rest.dto.response.UserPointsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    @PostMapping("/points")
    public Result<UserPointsResponse> getUserPoints(@Validated @RequestBody EmptyRequest request) {
        UserPointsResponse response = userApplicationService.getUserPoints();
        return Result.success(response);
    }

    /**
     * 获取用户背包
     */
    @PostMapping("/inventory")
    public Result<PageResponse<InventoryResponse>> getUserInventory(@Validated @RequestBody EmptyRequest request) {
        PageResponse<InventoryResponse> response = userApplicationService.getUserInventory();
        return Result.success(response);
    }
}
