package com.star.reward.interfaces.rest.dto.request;

import lombok.Data;

/**
 * 更新任务模板请求
 */
@Data
public class UpdateTaskTemplateRequest {
    
    /**
     * 任务名称
     */
    private String name;
    
    /**
     * 任务描述
     */
    private String description;
}
