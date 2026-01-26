package com.star.reward.infrastructure.persistence.converter;

import com.star.reward.domain.userinventory.model.entity.UserInventoryBO;
import com.star.reward.domain.userinventory.model.valueobject.InventoryType;
import com.star.reward.infrastructure.persistence.dao.entity.RewardUserInventoryDO;
import org.springframework.beans.BeanUtils;
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
     * BO转DO（同名字段使用 BeanUtils 赋值）
     */
    public RewardUserInventoryDO UserInventoryBO2DO(UserInventoryBO source) {
        if (source == null) {
            return null;
        }
        
        RewardUserInventoryDO target = new RewardUserInventoryDO();
        // 同名字段使用 BeanUtils 复制
        BeanUtils.copyProperties(source, target);
        
        // 特殊字段手动处理
        target.setInventoryType(source.getInventoryType() != null ? source.getInventoryType().getCode() : null);
        target.setIsDeleted(source.getIsDeleted() != null && source.getIsDeleted() ? (byte) 1 : (byte) 0);
        
        return target;
    }
}
