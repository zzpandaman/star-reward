package com.star.reward.interfaces.rest.dto.request;

import lombok.Data;

/**
 * 任务执行列表查询请求
 */
@Data
public class TaskExecutionQueryRequest {

    /**
     * 页码，从 1 开始，默认 1
     */
    private Integer page = 1;

    /**
     * 每页条数，默认 10
     */
    private Integer pageSize = 10;

    /**
     * 状态过滤：ongoing-进行中(RUNNING+PAUSED)，running-执行中，paused-暂停，all-全部；默认 ongoing
     */
    private String state = "ongoing";
}
