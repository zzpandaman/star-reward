package com.star.reward.infrastructure.persistence.converter;

import com.star.reward.domain.userinventory.model.entity.UserInventoryBO;
import com.star.reward.domain.userinventory.model.valueobject.InventoryType;
import com.star.reward.infrastructure.persistence.dao.entity.RewardUserInventoryDO;
import org.springframework.stereotype.Component;

/**
 * 用户库存转换器
 */
@Component
public class UserInventoryConverter {
    
    /**
     * DO转领域实体
     */
    public UserInventoryBO toDomain(RewardUserInventoryDO doEntity) {
        if (doEntity == null) {
            return null;
        }
        
        return UserInventoryBO.builder()
                .id(doEntity.getId())
                .inventoryNo(doEntity.getInventoryNo())
                .inventoryType(doEntity.getInventoryType() != null ? InventoryType.fromCode(doEntity.getInventoryType()) : null)
                .name(doEntity.getName())
                .description(doEntity.getDescription())
                .quantity(doEntity.getQuantity())
                .unit(doEntity.getUnit())
                .publishBy(doEntity.getPublishBy())
                .publishById(doEntity.getPublishById())
                .belongTo(doEntity.getBelongTo())
                .belongToId(doEntity.getBelongToId())
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
    public RewardUserInventoryDO toDO(UserInventoryBO domain) {
        if (domain == null) {
            return null;
        }
        
        RewardUserInventoryDO doEntity = new RewardUserInventoryDO();
        doEntity.setId(domain.getId());
        doEntity.setInventoryNo(domain.getInventoryNo());
        doEntity.setInventoryType(domain.getInventoryType() != null ? domain.getInventoryType().getCode() : null);
        doEntity.setName(domain.getName());
        doEntity.setDescription(domain.getDescription());
        doEntity.setQuantity(domain.getQuantity());
        doEntity.setUnit(domain.getUnit());
        doEntity.setPublishBy(domain.getPublishBy());
        doEntity.setPublishById(domain.getPublishById());
        doEntity.setBelongTo(domain.getBelongTo());
        doEntity.setBelongToId(domain.getBelongToId());
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
