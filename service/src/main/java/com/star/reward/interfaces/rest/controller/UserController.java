package com.star.reward.interfaces.rest.controller;

import com.star.common.page.PageResponse;
import com.star.common.result.Result;
import com.star.reward.application.service.PointRecordApplicationService;
import com.star.reward.application.service.UserApplicationService;
import com.star.reward.interfaces.rest.dto.response.InventoryResponse;
import com.star.reward.interfaces.rest.dto.response.PointRecordResponse;
import com.star.reward.interfaces.rest.dto.response.UserPointsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/reward/user")
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService userApplicationService;
    private final PointRecordApplicationService pointRecordApplicationService;

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

    /**
     * 获取用户积分记录列表
     *
     * @param type 可选，earn/spend 过滤类型
     * @param page 页码，从1开始
     * @param size 每页条数
     */
    @GetMapping("/point-records")
    public Result<PageResponse<PointRecordResponse>> getPointRecords(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        PageResponse<PointRecordResponse> response = pointRecordApplicationService.listUserPointRecords(type, page, size);
        return Result.success(response);
    }

    /**
     * 获取积分记录单条详情
     */
    @GetMapping("/point-records/{id}")
    public Result<PointRecordResponse> getPointRecordById(@PathVariable Long id) {
        PointRecordResponse response = pointRecordApplicationService.getPointRecordById(id);
        return Result.success(response);
    }
}
