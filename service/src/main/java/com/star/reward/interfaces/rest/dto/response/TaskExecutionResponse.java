package com.star.reward.interfaces.rest.dto.response;

import com.star.reward.domain.taskinstance.model.valueobject.ExecutionRecordVO;
import com.star.reward.domain.taskinstance.model.valueobject.PointsDetailItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
     * 总暂停时长（秒）
     */
    private Long totalPausedDuration;

    /**
     * 总执行时长（秒）
     */
    private Long totalExecutionDuration;
    
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

    /**
     * 执行记录流水
     */
    private List<ExecutionRecordVO> executionRecords;

    /**
     * 积分计算明细
     */
    private List<PointsDetailItem> pointsDetails;
}
