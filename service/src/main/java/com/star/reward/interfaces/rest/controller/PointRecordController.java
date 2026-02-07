package com.star.reward.interfaces.rest.controller;

import com.star.common.page.PageResponse;
import com.star.common.result.Result;
import com.star.reward.application.service.PointRecordApplicationService;
import com.star.reward.interfaces.rest.assembler.PointRecordRequestAssembler;
import com.star.reward.interfaces.rest.dto.request.PointRecordQueryRequest;
import com.star.reward.interfaces.rest.dto.response.PointRecordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 积分记录控制器
 */
@RestController
@RequestMapping("/api/reward/point-records")
@RequiredArgsConstructor
public class PointRecordController {

    private final PointRecordApplicationService pointRecordApplicationService;

    /**
     * 查询当前用户积分记录列表
     */
    @PostMapping("/query")
    public Result<PageResponse<PointRecordResponse>> queryPointRecords(
            @Validated @RequestBody(required = false) PointRecordQueryRequest request) {
        PageResponse<PointRecordResponse> response = pointRecordApplicationService
                .listUserPointRecords(PointRecordRequestAssembler.requestToQueryCommand(request));
        return Result.success(response);
    }

    /**
     * 获取积分记录单条详情
     */
    @GetMapping("/{id}")
    public Result<PointRecordResponse> getPointRecordById(@PathVariable Long id) {
        PointRecordResponse response = pointRecordApplicationService.getPointRecordById(id);
        return Result.success(response);
    }
}
