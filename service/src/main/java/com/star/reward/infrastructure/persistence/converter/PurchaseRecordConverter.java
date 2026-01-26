package com.star.reward.infrastructure.persistence.converter;

import com.star.reward.domain.purchaserecord.model.entity.PurchaseRecordBO;
import com.star.reward.infrastructure.persistence.dao.entity.RewardPurchaseRecordDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * 购买记录转换器
 */
@Component
public class PurchaseRecordConverter {
    
    /**
     * DO转领域实体
     */
    public PurchaseRecordBO toDomain(RewardPurchaseRecordDO doEntity) {
        if (doEntity == null) {
            return null;
        }
        
        return PurchaseRecordBO.builder()
                .id(doEntity.getId())
                .purchaseNo(doEntity.getPurchaseNo())
                .productNo(doEntity.getProductNo())
                .name(doEntity.getName())
                .description(doEntity.getDescription())
                .price(doEntity.getPrice())
                .minQuantity(doEntity.getMinQuantity())
                .minUnit(doEntity.getMinUnit())
                .publishBy(doEntity.getPublishBy())
                .publishById(doEntity.getPublishById())
                .purchaseQuantity(doEntity.getPurchaseQuantity())
                .purchaseBy(doEntity.getPurchaseBy())
                .purchaseById(doEntity.getPurchaseById())
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
    public RewardPurchaseRecordDO PurchaseRecordBO2DO(PurchaseRecordBO source) {
        if (source == null) {
            return null;
        }
        
        RewardPurchaseRecordDO target = new RewardPurchaseRecordDO();
        // 同名字段使用 BeanUtils 复制
        BeanUtils.copyProperties(source, target);
        
        // 特殊字段手动处理
        target.setIsPreset(source.getIsPreset() != null && source.getIsPreset() ? (byte) 1 : (byte) 0);
        target.setIsDeleted(source.getIsDeleted() != null && source.getIsDeleted() ? (byte) 1 : (byte) 0);
        
        return target;
    }
}
