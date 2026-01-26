package com.star.reward.infrastructure.persistence.converter;

import com.star.reward.domain.product.model.entity.ProductBO;
import com.star.reward.infrastructure.persistence.dao.entity.RewardProductDO;
import org.springframework.stereotype.Component;

/**
 * 商品转换器
 */
@Component
public class ProductConverter {
    
    /**
     * DO转领域实体
     */
    public ProductBO toDomain(RewardProductDO doEntity) {
        if (doEntity == null) {
            return null;
        }
        
        return ProductBO.builder()
                .id(doEntity.getId())
                .productNo(doEntity.getProductBONo())
                .name(doEntity.getName())
                .description(doEntity.getDescription())
                .price(doEntity.getPrice())
                .minQuantity(doEntity.getMinQuantity())
                .minUnit(doEntity.getMinUnit())
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
    public RewardProductDO toDO(ProductBO domain) {
        if (domain == null) {
            return null;
        }
        
        RewardProductDO doEntity = new RewardProductDO();
        doEntity.setId(domain.getId());
        doEntity.setProductBONo(domain.getProductBONo());
        doEntity.setName(domain.getName());
        doEntity.setDescription(domain.getDescription());
        doEntity.setPrice(domain.getPrice());
        doEntity.setMinQuantity(domain.getMinQuantity());
        doEntity.setMinUnit(domain.getMinUnit());
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
