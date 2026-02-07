package com.star.reward.domain.taskinstance.model.constant;

/**
 * 任务实例领域常量
 */
public final class TaskInstanceConstants {

    private TaskInstanceConstants() {
    }

    /** 实例编号前缀 */
    public static final String INSTANCE_NO_PREFIX = "EXE";

    /** 状态：执行中 */
    public static final String STATUS_RUNNING = "running";

    /** 状态：暂停 */
    public static final String STATUS_PAUSED = "paused";

    /** 状态：已取消 */
    public static final String STATUS_CANCELLED = "cancelled";

    /** 状态：已完成 */
    public static final String STATUS_COMPLETED = "completed";

    /** 状态：未知 */
    public static final String STATUS_UNKNOWN = "unknown";

    /** 备注：已取消 */
    public static final String REMARK_CANCELLED = "已取消";

    /** 取消相关备注关键词（用于状态判断） */
    public static final String CANCELLED_KEYWORD = "取消";
}
