package com.star.reward.interfaces.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 任务模板响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskTemplateResponse {
    
    /**
     * ID
     */
    private Long id;
    
    /**
     * 模板编号
     */
    private String templateNo;
    
    /**
     * 任务名称
     */
    private String name;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 是否为预设任务
     */
    private Boolean isPreset;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
