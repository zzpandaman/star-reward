package com.star.reward.application.command;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建商品用例入参
 */
@Data
public class CreateProductCommand {

    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal minQuantity;
    private String unit;
}
