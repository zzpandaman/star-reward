package com.star.reward.interfaces.rest.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 创建任务模板请求
 */
@Data
public class CreateTaskTemplateRequest {
    
    /**
     * 任务名称
     */
    @NotBlank(message = "任务名称不能为空")
    private String name;
    
    /**
     * 任务描述
     */
    @NotBlank(message = "任务描述不能为空")
    private String description;
}
