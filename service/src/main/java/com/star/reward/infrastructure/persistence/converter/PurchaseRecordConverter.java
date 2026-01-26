package com.star.reward.infrastructure.persistence.converter;

import com.star.reward.domain.purchaserecord.model.entity.PurchaseRecordBO;
import com.star.reward.infrastructure.persistence.dao.entity.RewardPurchaseRecordDO;
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
     * 领域实体转DO
     */
    public RewardPurchaseRecordDO toDO(PurchaseRecordBO domain) {
        if (domain == null) {
            return null;
        }
        
        RewardPurchaseRecordDO doEntity = new RewardPurchaseRecordDO();
        doEntity.setId(domain.getId());
        doEntity.setPurchaseNo(domain.getPurchaseNo());
        doEntity.setProductNo(domain.getProductNo());
        doEntity.setName(domain.getName());
        doEntity.setDescription(domain.getDescription());
        doEntity.setPrice(domain.getPrice());
        doEntity.setMinQuantity(domain.getMinQuantity());
        doEntity.setMinUnit(domain.getMinUnit());
        doEntity.setPublishBy(domain.getPublishBy());
        doEntity.setPublishById(domain.getPublishById());
        doEntity.setPurchaseQuantity(domain.getPurchaseQuantity());
        doEntity.setPurchaseBy(domain.getPurchaseBy());
        doEntity.setPurchaseById(domain.getPurchaseById());
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
