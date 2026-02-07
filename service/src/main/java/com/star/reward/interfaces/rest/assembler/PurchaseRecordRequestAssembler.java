package com.star.reward.interfaces.rest.assembler;

import com.star.reward.application.command.ExchangeProductCommand;
import com.star.reward.interfaces.rest.dto.request.ExchangeProductRequest;

/**
 * 购买记录 Request → Command 转换器（Interfaces → Application 边界）
 */
public final class PurchaseRecordRequestAssembler {

    private PurchaseRecordRequestAssembler() {
    }

    public static ExchangeProductCommand requestToExchangeCommand(ExchangeProductRequest request) {
        if (request == null) {
            return null;
        }
        ExchangeProductCommand cmd = new ExchangeProductCommand();
        cmd.setProductId(request.getProductId());
        cmd.setQuantity(request.getQuantity());
        return cmd;
    }
}
