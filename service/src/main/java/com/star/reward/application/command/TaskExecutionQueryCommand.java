package com.star.reward.application.command;

import com.star.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务执行列表查询用例入参
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskExecutionQueryCommand extends PageRequest {

    /**
     * 状态过滤：ongoing, running, paused, all
     */
    private String state;
}
