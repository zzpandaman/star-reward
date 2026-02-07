package com.star.reward.application.command;

import lombok.Data;

/**
 * 任务模板列表查询用例入参
 */
@Data
public class TaskTemplateQueryCommand {

    /**
     * 页码（从 1 开始），可选，默认 1
     */
    private Integer page = 1;

    /**
     * 每页条数，可选，默认 10
     */
    private Integer pageSize = 10;
}
