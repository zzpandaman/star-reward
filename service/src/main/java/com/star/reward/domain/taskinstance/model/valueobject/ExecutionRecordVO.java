package com.star.reward.domain.taskinstance.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 执行记录值对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionRecordVO {

    /** 执行动作 */
    private ExecutionAction action;

    /** 动作时间 */
    private LocalDateTime actionTime;

    /**
     * 工厂方法：创建执行记录
     */
    public static ExecutionRecordVO of(ExecutionAction action, LocalDateTime actionTime) {
        return ExecutionRecordVO.builder()
                .action(action)
                .actionTime(actionTime)
                .build();
    }
}
