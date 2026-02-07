package com.star.reward.infrastructure.persistence.converter;

import com.star.reward.domain.userinventory.model.entity.UserInventoryBO;
import com.star.reward.domain.userinventory.model.valueobject.InventoryType;
import com.star.reward.infrastructure.persistence.dao.entity.RewardUserInventoryDO;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 用户库存转换器（Infrastructure：DO ↔ Entity）
 */
public final class UserInventoryConverter {

    private UserInventoryConverter() {
    }

    /**
     * DO → Entity
     */
    public static UserInventoryBO doToEntity(RewardUserInventoryDO doEntity) {
        if (doEntity == null) {
            return null;
        }
        
        UserInventoryBO bo = new UserInventoryBO();
        // 同名字段使用 BeanUtils 复制
        BeanUtils.copyProperties(doEntity, bo);
        
        // 特殊字段手动处理
        bo.setInventoryType(doEntity.getInventoryType() != null ? InventoryType.fromCode(doEntity.getInventoryType()) : null);
        bo.setIsDeleted(doEntity.getIsDeleted() != null && doEntity.getIsDeleted() == 1);
        bo.setCreateTime(convertToLocalDateTime(doEntity.getCreateTime()));
        bo.setUpdateTime(convertToLocalDateTime(doEntity.getUpdateTime()));
        
        return bo;
    }
    
    /**
     * Entity → DO
     */
    public static RewardUserInventoryDO entityToDo(UserInventoryBO source) {
        if (source == null) {
            return null;
        }
        
        RewardUserInventoryDO target = new RewardUserInventoryDO();
        // 同名字段使用 BeanUtils 复制
        BeanUtils.copyProperties(source, target);
        
        // 特殊字段手动处理
        target.setInventoryType(source.getInventoryType() != null ? source.getInventoryType().getCode() : null);
        target.setIsDeleted(source.getIsDeleted() != null && source.getIsDeleted() ? (byte) 1 : (byte) 0);
        target.setCreateTime(convertToDate(source.getCreateTime()));
        target.setUpdateTime(convertToDate(source.getUpdateTime()));
        
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
}
