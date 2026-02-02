package com.star.reward.interfaces.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 用户积分响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPointsResponse {
    
    /**
     * 积分余额
     */
    private BigDecimal points;
}
