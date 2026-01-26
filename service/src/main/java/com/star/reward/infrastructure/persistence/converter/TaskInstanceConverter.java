package com.star.reward.infrastructure.persistence.converter;

import com.star.reward.domain.taskinstance.model.entity.TaskInstanceBO;
import com.star.reward.domain.taskinstance.model.valueobject.InstanceState;
import com.star.reward.domain.tasktemplate.model.valueobject.MinUnit;
import com.star.reward.infrastructure.persistence.dao.entity.RewardTaskInstanceDO;
import org.springframework.beans.BeanUtils;
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
     * BO转DO（同名字段使用 BeanUtils 赋值）
     */
    public RewardTaskInstanceDO TaskInstanceBO2DO(TaskInstanceBO source) {
        if (source == null) {
            return null;
        }
        
        RewardTaskInstanceDO target = new RewardTaskInstanceDO();
        // 同名字段使用 BeanUtils 复制
        BeanUtils.copyProperties(source, target);
        
        // 特殊字段手动处理
        target.setMinUnit(source.getMinUnit() != null ? source.getMinUnit().getCode() : null);
        target.setInstanceState(source.getInstanceState() != null ? source.getInstanceState().getCode() : null);
        target.setIsPreset(source.getIsPreset() != null && source.getIsPreset() ? (byte) 1 : (byte) 0);
        target.setIsDeleted(source.getIsDeleted() != null && source.getIsDeleted() ? (byte) 1 : (byte) 0);
        
        return target;
    }
}
