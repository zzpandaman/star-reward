package com.star.reward.application.service;

import com.star.reward.application.assembler.PurchaseRecordAssembler;
import com.star.reward.application.command.ExchangeProductCommand;
import com.star.reward.domain.purchaserecord.service.ExchangeService;
import com.star.reward.interfaces.rest.dto.response.ExchangeResponse;
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
