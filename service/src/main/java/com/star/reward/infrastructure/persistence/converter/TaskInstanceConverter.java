package com.star.reward.infrastructure.persistence.converter;

import com.alibaba.fastjson2.JSON;
import com.star.reward.domain.taskinstance.model.entity.TaskInstanceBO;
import com.star.reward.domain.taskinstance.model.valueobject.InstanceState;
import com.star.reward.domain.tasktemplate.model.valueobject.MinUnit;
import com.star.reward.infrastructure.persistence.dao.entity.RewardTaskInstanceDO;
import com.star.reward.shared.context.CurrentUserContext;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 任务实例转换器（Infrastructure：DO ↔ Entity）
 */
public final class TaskInstanceConverter {

    private TaskInstanceConverter() {
    }

    /**
     * DO → Entity
     */
    public static TaskInstanceBO doToEntity(RewardTaskInstanceDO doEntity) {
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
     * Entity → DO
     */
    public static RewardTaskInstanceDO entityToDo(TaskInstanceBO source) {
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

        // 审计字段：insert 时若为空则从上下文补齐（表字段 NOT NULL）
        fillAuditFieldsIfNull(target, source);
        
        return target;
    }
    
    private static LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
    
    private static Date convertToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private static void fillAuditFieldsIfNull(RewardTaskInstanceDO target, TaskInstanceBO source) {
        if (target.getUpdateBy() != null && target.getUpdateById() != null && target.getUpdateTime() != null) {
            return;
        }
        CurrentUserContext user = CurrentUserContext.get();
        LocalDateTime now = LocalDateTime.now();
        if (target.getUpdateBy() == null) {
            target.setUpdateBy(user != null && user.getUserNo() != null ? user.getUserNo()
                    : (user != null && user.getUserId() != null ? user.getUserId().toString() : "system"));
        }
        if (target.getUpdateById() == null) {
            target.setUpdateById(user != null && user.getUserId() != null ? user.getUserId() : 0L);
        }
        if (target.getUpdateTime() == null) {
            target.setUpdateTime(convertToDate(now));
        }
        if (target.getCreateBy() == null) {
            target.setCreateBy(target.getUpdateBy());
        }
        if (target.getCreateById() == null) {
            target.setCreateById(target.getUpdateById());
        }
        if (target.getCreateTime() == null) {
            target.setCreateTime(target.getUpdateTime());
        }
    }
}
