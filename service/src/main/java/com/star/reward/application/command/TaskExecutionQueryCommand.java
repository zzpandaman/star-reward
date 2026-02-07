package com.star.reward.application.command;

import lombok.Data;

/**
 * 任务执行列表查询用例入参
 */
@Data
public class TaskExecutionQueryCommand {

    /**
     * 页码，从 1 开始
     */
    private Integer page;

    /**
     * 每页条数
     */
    private Integer pageSize;

    /**
     * 状态过滤：ongoing, running, paused, all
     */
    private String state;
}
