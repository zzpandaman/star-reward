package com.star.reward.domain.taskinstance.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 执行区间（时间区间 + 类型，便于明细追溯）
 */
@Getter
@AllArgsConstructor
public class ExecutionInterval {

    private final TimeRange range;
    private final ExecutionIntervalType intervalType;

    public static ExecutionInterval of(TimeRange range, ExecutionIntervalType intervalType) {
        return new ExecutionInterval(range, intervalType);
    }
}
