package com.star.reward.interfaces.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 任务执行响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskExecutionResponse {
    
    /**
     * ID
     */
    private Long id;
    
    /**
     * 实例编号
     */
    private String executionNo;
    
    /**
     * 执行人编号
     */
    private String userNo;
    
    /**
     * 任务模板ID
     */
    private Long taskTemplateId;
    
    /**
     * 任务名称
     */
    private String taskName;
    
    /**
     * 开始时间（秒级时间戳）
     */
    private Long startTime;
    
    /**
     * 结束时间（秒级时间戳）
     */
    private Long endTime;
    
    /**
     * 暂停时间（秒级时间戳）
     */
    private Long pausedTime;
    
    /**
     * 总暂停时长（秒）
     */
    private Long totalPausedDuration;
    
    /**
     * 实际时长（分钟）
     */
    private BigDecimal actualDuration;
    
    /**
     * 实际积分
     */
    private BigDecimal actualReward;
    
    /**
     * 状态：running, paused, completed, cancelled
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
