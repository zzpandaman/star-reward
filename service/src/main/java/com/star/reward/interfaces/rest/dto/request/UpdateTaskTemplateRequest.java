package com.star.reward.interfaces.rest.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 更新任务模板请求
 */
@Data
public class UpdateTaskTemplateRequest {

    @NotNull(message = "id 不能为空")
    private Long id;
    
    /**
     * 任务名称
     */
    private String name;
    
    /**
     * 任务描述
     */
    private String description;
}
