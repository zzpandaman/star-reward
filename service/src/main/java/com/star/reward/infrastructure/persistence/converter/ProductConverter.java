package com.star.reward.infrastructure.persistence.converter;

import com.star.reward.domain.product.model.entity.ProductBO;
import com.star.reward.infrastructure.persistence.dao.entity.RewardProductDO;
import com.star.reward.shared.context.CurrentUserContext;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 商品转换器（Infrastructure：DO ↔ Entity）
 */
public final class ProductConverter {

    private ProductConverter() {
    }

    /**
     * DO → Entity
     */
    public static ProductBO doToEntity(RewardProductDO doEntity) {
        if (doEntity == null) {
            return null;
        }
        
        ProductBO bo = new ProductBO();
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
    public static RewardProductDO entityToDo(ProductBO source) {
        if (source == null) {
            return null;
        }
        
        RewardProductDO target = new RewardProductDO();
        // 同名字段使用 BeanUtils 复制
        BeanUtils.copyProperties(source, target);
        
        // 特殊字段手动处理
        target.setIsPreset(source.getIsPreset() != null && source.getIsPreset() ? (byte) 1 : (byte) 0);
        target.setIsDeleted(source.getIsDeleted() != null && source.getIsDeleted() ? (byte) 1 : (byte) 0);
        
        // 时间字段手动处理
        target.setCreateTime(convertToDate(source.getCreateTime()));
        target.setUpdateTime(convertToDate(source.getUpdateTime()));

        fillAuditFieldsIfNull(target);
        
        return target;
    }

    /**
     * Partial Entity → DO（仅复制非空字段，供 updateByPrimaryKeySelective 用）
     */
    public static RewardProductDO partialEntityToDo(ProductBO patch) {
        if (patch == null || patch.getId() == null) {
            return null;
        }
        RewardProductDO target = new RewardProductDO();
        target.setId(patch.getId());
        if (patch.getName() != null) {
            target.setName(patch.getName());
        }
        if (patch.getDescription() != null) {
            target.setDescription(patch.getDescription());
        }
        if (patch.getPrice() != null) {
            target.setPrice(patch.getPrice());
        }
        if (patch.getMinQuantity() != null) {
            target.setMinQuantity(patch.getMinQuantity());
        }
        if (patch.getMinUnit() != null) {
            target.setMinUnit(patch.getMinUnit());
        }
        if (patch.getUpdateBy() != null) {
            target.setUpdateBy(patch.getUpdateBy());
        }
        if (patch.getUpdateById() != null) {
            target.setUpdateById(patch.getUpdateById());
        }
        if (patch.getUpdateTime() != null) {
            target.setUpdateTime(convertToDate(patch.getUpdateTime()));
        }
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

    private static void fillAuditFieldsIfNull(RewardProductDO target) {
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
