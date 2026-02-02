package com.star.reward.interfaces.rest.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 开始任务请求
 */
@Data
public class StartTaskRequest {
    
    /**
     * 任务模板ID
     */
    @NotNull(message = "任务模板ID不能为空")
    private Long taskTemplateId;
}
