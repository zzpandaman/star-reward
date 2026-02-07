package com.star.reward.interfaces.rest.dto.request;

import com.star.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务执行列表查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskExecutionQueryRequest extends PageRequest {

    /**
     * 状态过滤：ongoing-进行中(RUNNING+PAUSED)，running-执行中，paused-暂停，all-全部；默认 ongoing
     */
    private String state = "ongoing";
}
