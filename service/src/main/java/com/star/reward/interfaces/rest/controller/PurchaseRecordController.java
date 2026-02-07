package com.star.reward.interfaces.rest.controller;

import com.star.common.result.Result;
import com.star.reward.application.service.PurchaseRecordApplicationService;
import com.star.reward.interfaces.rest.assembler.PurchaseRecordRequestAssembler;
import com.star.reward.interfaces.rest.dto.request.ExchangeProductRequest;
import com.star.reward.interfaces.rest.dto.response.ExchangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 购买记录控制器
 */
@RestController
@RequestMapping("/api/reward/purchase-record")
@RequiredArgsConstructor
public class PurchaseRecordController {

    private final PurchaseRecordApplicationService purchaseRecordApplicationService;

    /**
     * 兑换商品
     */
    @PostMapping("/exchange")
    public Result<ExchangeResponse> exchangeProduct(@Validated @RequestBody ExchangeProductRequest request) {
        ExchangeResponse response = purchaseRecordApplicationService.exchangeProduct(
                PurchaseRecordRequestAssembler.requestToExchangeCommand(request));
        return Result.success(response);
    }
}
