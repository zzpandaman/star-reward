package com.star.reward.application.service;

import com.star.common.exception.BusinessException;
import com.star.common.result.ResultCode;
import com.star.reward.application.assembler.PurchaseRecordAssembler;
import com.star.reward.application.command.ExchangeProductCommand;
import com.star.reward.domain.purchaserecord.model.entity.PurchaseRecordBO;
import com.star.reward.domain.purchaserecord.repository.PurchaseRecordRepository;
import com.star.reward.domain.purchaserecord.service.ExchangeService;
import com.star.reward.interfaces.rest.dto.response.ExchangeResponse;
import com.star.reward.interfaces.rest.dto.response.PurchaseRecordResponse;
import com.star.reward.shared.context.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 购买记录应用服务
 */
@Service
@RequiredArgsConstructor
public class PurchaseRecordApplicationService {

    private final ExchangeService exchangeService;
    private final PurchaseRecordRepository purchaseRecordRepository;

    /**
     * 根据购买编号获取详情，校验 purchaseById=当前用户
     */
    public PurchaseRecordResponse getPurchaseRecordByPurchaseNo(String purchaseNo) {
        CurrentUserContext user = CurrentUserContext.get();
        PurchaseRecordBO bo = purchaseRecordRepository.findByPurchaseNo(purchaseNo)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "购买记录不存在"));
        if (!user.getUserId().equals(bo.getPurchaseById())) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "无权访问该购买记录");
        }
        return PurchaseRecordAssembler.entityToResponse(bo);
    }

    /**
     * 兑换商品
     */
    @Transactional
    public ExchangeResponse exchangeProduct(ExchangeProductCommand command) {
        CurrentUserContext user = CurrentUserContext.get();
        ExchangeService.ExchangeResult result = exchangeService.exchange(
                user.getUserId(), user.getUserNo(), command.getProductId(), command.getQuantity());
        return PurchaseRecordAssembler.toExchangeResponse(result);
    }
}
