package com.star.reward.interfaces.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 积分记录响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointRecordResponse {
    
    /**
     * ID
     */
    private Long id;
    
    /**
     * 记录编号
     */
    private String recordNo;
    
    /**
     * 类型：earn-获得, spend-消费
     */
    private String type;
    
    /**
     * 积分数量
     */
    private BigDecimal amount;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 关联ID（任务实例编号或购买记录编号）
     */
    private String relatedId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
