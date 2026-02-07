package com.star.reward.application.command;

import lombok.Data;

/**
 * 任务操作用例入参（暂停/恢复/完成/取消）
 */
@Data
public class TaskOperationCommand {

    private Long id;

    /**
     * 前端操作时间（秒级时间戳），可选
     */
    private Long clientTime;
}
