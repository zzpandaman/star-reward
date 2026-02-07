package com.star.reward.infrastructure.persistence.converter;

import com.star.reward.domain.purchaserecord.model.entity.PurchaseRecordBO;
import com.star.reward.infrastructure.persistence.dao.entity.RewardPurchaseRecordDO;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 购买记录转换器（Infrastructure：DO ↔ Entity）
 */
public final class PurchaseRecordConverter {

    private PurchaseRecordConverter() {
    }

    /**
     * DO → Entity
     */
    public static PurchaseRecordBO doToEntity(RewardPurchaseRecordDO doEntity) {
        if (doEntity == null) {
            return null;
        }
        
        PurchaseRecordBO bo = new PurchaseRecordBO();
        // 同名字段使用 BeanUtils 复制
        BeanUtils.copyProperties(doEntity, bo);
        
        // 特殊字段手动处理
        bo.setIsPreset(doEntity.getIsPreset() != null && doEntity.getIsPreset() == 1);
        bo.setIsDeleted(doEntity.getIsDeleted() != null && doEntity.getIsDeleted() == 1);
        bo.setCreateTime(convertToLocalDateTime(doEntity.getCreateTime()));
        bo.setUpdateTime(convertToLocalDateTime(doEntity.getUpdateTime()));
        
        return bo;
    }
    
    /**
     * Entity → DO
     */
    public static RewardPurchaseRecordDO entityToDo(PurchaseRecordBO source) {
        if (source == null) {
            return null;
        }
        
        RewardPurchaseRecordDO target = new RewardPurchaseRecordDO();
        // 同名字段使用 BeanUtils 复制
        BeanUtils.copyProperties(source, target);
        
        // 特殊字段手动处理
        target.setIsPreset(source.getIsPreset() != null && source.getIsPreset() ? (byte) 1 : (byte) 0);
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
