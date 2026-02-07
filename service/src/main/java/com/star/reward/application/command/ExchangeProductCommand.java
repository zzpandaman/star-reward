package com.star.reward.application.command;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 兑换商品用例入参
 */
@Data
public class ExchangeProductCommand {

    private Long productId;
    private BigDecimal quantity;
}
