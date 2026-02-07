package com.star.reward.interfaces.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购买记录响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRecordResponse {

    private Long id;
    private String purchaseNo;
    private String productNo;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal minQuantity;
    private String minUnit;
    private Integer purchaseQuantity;
    private LocalDateTime createTime;
}
