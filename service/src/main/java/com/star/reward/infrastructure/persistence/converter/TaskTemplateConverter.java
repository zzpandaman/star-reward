package com.star.reward.infrastructure.persistence.converter;

import com.star.reward.domain.tasktemplate.model.entity.TaskTemplateBO;
import com.star.reward.domain.tasktemplate.model.valueobject.MinUnit;
import com.star.reward.infrastructure.persistence.dao.entity.RewardTaskTemplateDO;
import org.springframework.stereotype.Component;

/**
 * 任务模板转换器
 */
@Component
public class TaskTemplateConverter {
    
    /**
     * DO转领域实体
     */
    public TaskTemplateBO toDomain(RewardTaskTemplateDO doEntity) {
        if (doEntity == null) {
            return null;
        }
        
        return TaskTemplateBO.builder()
                .id(doEntity.getId())
                .templateNo(doEntity.getTemplateNo())
                .name(doEntity.getName())
                .description(doEntity.getDescription())
                .minUnitPoint(doEntity.getMinUnitPoint())
                .minUnit(doEntity.getMinUnit() != null ? MinUnit.fromCode(doEntity.getMinUnit()) : null)
                .publishBy(doEntity.getPublishBy())
                .publishById(doEntity.getPublishById())
                .isPreset(doEntity.getIsPreset() != null && doEntity.getIsPreset() == 1)
                .isDeleted(doEntity.getIsDeleted() != null && doEntity.getIsDeleted() == 1)
                .createBy(doEntity.getCreateBy())
                .createById(doEntity.getCreateById())
                .createTime(doEntity.getCreateTime())
                .updateBy(doEntity.getUpdateBy())
                .updateById(doEntity.getUpdateById())
                .updateTime(doEntity.getUpdateTime())
                .remark(doEntity.getRemark())
                .attributes(doEntity.getAttributes())
                .build();
    }
    
    /**
     * 领域实体转DO
     */
    public RewardTaskTemplateDO toDO(TaskTemplateBO domain) {
        if (domain == null) {
            return null;
        }
        
        RewardTaskTemplateDO doEntity = new RewardTaskTemplateDO();
        doEntity.setId(domain.getId());
        doEntity.setTemplateNo(domain.getTemplateNo());
        doEntity.setName(domain.getName());
        doEntity.setDescription(domain.getDescription());
        doEntity.setMinUnitPoint(domain.getMinUnitPoint());
        doEntity.setMinUnit(domain.getMinUnit() != null ? domain.getMinUnit().getCode() : null);
        doEntity.setPublishBy(domain.getPublishBy());
        doEntity.setPublishById(domain.getPublishById());
        doEntity.setIsPreset(domain.getIsPreset() != null && domain.getIsPreset() ? (byte) 1 : (byte) 0);
        doEntity.setIsDeleted(domain.getIsDeleted() != null && domain.getIsDeleted() ? (byte) 1 : (byte) 0);
        doEntity.setCreateBy(domain.getCreateBy());
        doEntity.setCreateById(domain.getCreateById());
        doEntity.setCreateTime(domain.getCreateTime());
        doEntity.setUpdateBy(domain.getUpdateBy());
        doEntity.setUpdateById(domain.getUpdateById());
        doEntity.setUpdateTime(domain.getUpdateTime());
        doEntity.setRemark(domain.getRemark());
        doEntity.setAttributes(domain.getAttributes());
        
        return doEntity;
    }
}
