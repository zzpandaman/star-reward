package com.star.reward.infrastructure.persistence.converter;

import com.star.reward.domain.taskinstance.model.entity.TaskInstanceBO;
import com.star.reward.domain.taskinstance.model.valueobject.InstanceState;
import com.star.reward.domain.tasktemplate.model.valueobject.MinUnit;
import com.star.reward.infrastructure.persistence.dao.entity.RewardTaskInstanceDO;
import org.springframework.stereotype.Component;

/**
 * 任务实例转换器
 */
@Component
public class TaskInstanceConverter {
    
    /**
     * DO转领域实体
     */
    public TaskInstanceBO toDomain(RewardTaskInstanceDO doEntity) {
        if (doEntity == null) {
            return null;
        }
        
        return TaskInstanceBO.builder()
                .id(doEntity.getId())
                .instanceNo(doEntity.getInstanceNo())
                .templateNo(doEntity.getTemplateNo())
                .name(doEntity.getName())
                .description(doEntity.getDescription())
                .minUnitPoint(doEntity.getMinUnitPoint())
                .minUnit(doEntity.getMinUnit() != null ? MinUnit.fromCode(doEntity.getMinUnit()) : null)
                .publishBy(doEntity.getPublishBy())
                .publishById(doEntity.getPublishById())
                .startTime(doEntity.getStartTime())
                .endTime(doEntity.getEndTime())
                .instanceState(doEntity.getInstanceState() != null ? InstanceState.fromCode(doEntity.getInstanceState()) : null)
                .executeBy(doEntity.getExecuteBy())
                .executeById(doEntity.getExecuteById())
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
    public RewardTaskInstanceDO toDO(TaskInstanceBO domain) {
        if (domain == null) {
            return null;
        }
        
        RewardTaskInstanceDO doEntity = new RewardTaskInstanceDO();
        doEntity.setId(domain.getId());
        doEntity.setInstanceNo(domain.getInstanceNo());
        doEntity.setTemplateNo(domain.getTemplateNo());
        doEntity.setName(domain.getName());
        doEntity.setDescription(domain.getDescription());
        doEntity.setMinUnitPoint(domain.getMinUnitPoint());
        doEntity.setMinUnit(domain.getMinUnit() != null ? domain.getMinUnit().getCode() : null);
        doEntity.setPublishBy(domain.getPublishBy());
        doEntity.setPublishById(domain.getPublishById());
        doEntity.setStartTime(domain.getStartTime());
        doEntity.setEndTime(domain.getEndTime());
        doEntity.setInstanceState(domain.getInstanceState() != null ? domain.getInstanceState().getCode() : null);
        doEntity.setExecuteBy(domain.getExecuteBy());
        doEntity.setExecuteById(domain.getExecuteById());
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
