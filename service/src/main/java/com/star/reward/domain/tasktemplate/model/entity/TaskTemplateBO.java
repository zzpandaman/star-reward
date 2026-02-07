package com.star.reward.domain.tasktemplate.model.entity;

import com.star.reward.domain.tasktemplate.model.valueobject.MinUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 任务模板领域实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskTemplateBO {
    
    /**
     * 主键ID
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
     * 1最小单位积分
     */
    private BigDecimal minUnitPoint;
    
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
     * 扩展字段（JSON格式）
     */
    private String attributes;

    /**
     * 充血方法：初始化创建时的编号、默认值、审计字段
     *
     * @param templateNo 模板编号
     * @param userNo 用户账号
     * @param userId 用户ID
     * @param now 创建时间
     */
    public void initForCreate(String templateNo, String userNo, Long userId, LocalDateTime now) {
        this.templateNo = templateNo;
        this.isPreset = false;
        this.isDeleted = false;
        this.publishBy = userNo;
        this.publishById = userId;
        this.createBy = userNo;
        this.createById = userId;
        this.createTime = now;
        this.updateBy = userNo;
        this.updateById = userId;
        this.updateTime = now;
    }
}
