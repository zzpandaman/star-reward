package com.star.reward.domain.taskinstance.model.entity;

import com.star.reward.domain.taskinstance.model.valueobject.InstanceState;
import com.star.reward.domain.tasktemplate.model.valueobject.MinUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 任务实例领域实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskInstanceBO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 实例编号
     */
    private String instanceNo;
    
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
     * 1最小单位积分
     */
    private Integer minUnitPoint;
    
    /**
     * 最小单位
     */
    private MinUnit minUnit;
    
    /**
     * 发布人账号
     */
    private String publishBy;
    
    /**
     * 发布人ID
     */
    private Long publishById;
    
    /**
     * 实例开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 实例结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 实例状态
     */
    private InstanceState instanceState;
    
    /**
     * 执行人账号
     */
    private String executeBy;
    
    /**
     * 执行人ID
     */
    private Long executeById;
    
    /**
     * 是否为预设任务。0-否 1-是
     */
    private Boolean isPreset;
    
    /**
     * 是否删除。0-否 1-是
     */
    private Boolean isDeleted;
    
    /**
     * 创建人账号
     */
    private String createBy;
    
    /**
     * 创建人ID
     */
    private Long createById;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新人账号
     */
    private String updateBy;
    
    /**
     * 更新人ID
     */
    private Long updateById;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 扩展字段（JSON格式，存储启动、停止流水）
     */
    private String attributes;
}
