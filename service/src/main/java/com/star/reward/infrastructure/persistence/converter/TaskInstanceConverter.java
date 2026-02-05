package com.star.reward.infrastructure.persistence.converter;

import com.alibaba.fastjson2.JSON;
import com.star.reward.domain.taskinstance.model.entity.TaskInstanceBO;
import com.star.reward.domain.taskinstance.model.valueobject.InstanceState;
import com.star.reward.domain.tasktemplate.model.valueobject.MinUnit;
import com.star.reward.infrastructure.persistence.dao.entity.RewardTaskInstanceDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

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
        
        TaskInstanceBO bo = new TaskInstanceBO();
        BeanUtils.copyProperties(doEntity, bo, "attributes");
        
        if (doEntity.getAttributes() != null) {
            bo.setAttributes(JSON.parseObject(doEntity.getAttributes()));
        }
        
        // 特殊字段手动处理
        bo.setMinUnit(doEntity.getMinUnit() != null ? MinUnit.fromCode(doEntity.getMinUnit()) : null);
        bo.setStartTime(convertToLocalDateTime(doEntity.getStartTime()));
        bo.setEndTime(convertToLocalDateTime(doEntity.getEndTime()));
        bo.setInstanceState(doEntity.getInstanceState() != null ? InstanceState.fromCode(doEntity.getInstanceState()) : null);
        bo.setIsPreset(doEntity.getIsPreset() != null && doEntity.getIsPreset() == 1);
        bo.setIsDeleted(doEntity.getIsDeleted() != null && doEntity.getIsDeleted() == 1);
        bo.setCreateTime(convertToLocalDateTime(doEntity.getCreateTime()));
        bo.setUpdateTime(convertToLocalDateTime(doEntity.getUpdateTime()));
        
        return bo;
    }
    
    /**
     * BO转DO（同名字段使用 BeanUtils 赋值）
     */
    public RewardTaskInstanceDO TaskInstanceBO2DO(TaskInstanceBO source) {
        if (source == null) {
            return null;
        }
        
        RewardTaskInstanceDO target = new RewardTaskInstanceDO();
        BeanUtils.copyProperties(source, target, "attributes");
        
        if (source.getAttributes() != null) {
            target.setAttributes(source.getAttributes().toJSONString());
        }
        
        // 特殊字段手动处理
        target.setMinUnit(source.getMinUnit() != null ? source.getMinUnit().getCode() : null);
        target.setInstanceState(source.getInstanceState() != null ? source.getInstanceState().getCode() : null);
        target.setIsPreset(source.getIsPreset() != null && source.getIsPreset() ? (byte) 1 : (byte) 0);
        target.setIsDeleted(source.getIsDeleted() != null && source.getIsDeleted() ? (byte) 1 : (byte) 0);
        target.setStartTime(convertToDate(source.getStartTime()));
        target.setEndTime(convertToDate(source.getEndTime()));
        target.setCreateTime(convertToDate(source.getCreateTime()));
        target.setUpdateTime(convertToDate(source.getUpdateTime()));
        
        return target;
    }
    
    private LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
    
    private Date convertToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
