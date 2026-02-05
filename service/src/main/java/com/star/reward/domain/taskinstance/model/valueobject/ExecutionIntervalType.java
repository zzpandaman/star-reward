package com.star.reward.domain.taskinstance.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 执行区间类型（4 动作 × 4 动作 = 16 种排列，全部列出便于后续扩展动作时维护）
 */
@Getter
@AllArgsConstructor
public enum ExecutionIntervalType {

    START_START(ExecutionAction.START, ExecutionAction.START, false, "占位"),
    START_PAUSE(ExecutionAction.START, ExecutionAction.PAUSE, true, "开始→暂停"),
    START_RESUME(ExecutionAction.START, ExecutionAction.RESUME, false, "占位"),
    START_END(ExecutionAction.START, ExecutionAction.END, true, "开始→结束（无暂停）"),
    PAUSE_START(ExecutionAction.PAUSE, ExecutionAction.START, false, "占位"),
    PAUSE_PAUSE(ExecutionAction.PAUSE, ExecutionAction.PAUSE, false, "占位"),
    PAUSE_RESUME(ExecutionAction.PAUSE, ExecutionAction.RESUME, false, "暂停→恢复"),
    PAUSE_END(ExecutionAction.PAUSE, ExecutionAction.END, false, "暂停→结束"),
    RESUME_START(ExecutionAction.RESUME, ExecutionAction.START, false, "占位"),
    RESUME_PAUSE(ExecutionAction.RESUME, ExecutionAction.PAUSE, true, "恢复→暂停"),
    RESUME_RESUME(ExecutionAction.RESUME, ExecutionAction.RESUME, false, "占位"),
    RESUME_END(ExecutionAction.RESUME, ExecutionAction.END, true, "恢复→结束"),
    END_START(ExecutionAction.END, ExecutionAction.START, false, "占位"),
    END_PAUSE(ExecutionAction.END, ExecutionAction.PAUSE, false, "占位"),
    END_RESUME(ExecutionAction.END, ExecutionAction.RESUME, false, "占位"),
    END_END(ExecutionAction.END, ExecutionAction.END, false, "占位");

    private final ExecutionAction from;
    private final ExecutionAction to;
    private final boolean scoring;
    private final String description;

    /**
     * 是否计分
     */
    public boolean isScoring() {
        return scoring;
    }

    /**
     * 根据起止动作获取区间类型
     */
    public static ExecutionIntervalType from(ExecutionAction from, ExecutionAction to) {
        if (from == null || to == null) {
            return null;
        }
        for (ExecutionIntervalType type : values()) {
            if (type.from == from && type.to == to) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ExecutionIntervalType: " + from + "->" + to);
    }
}
