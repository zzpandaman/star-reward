package com.star.reward.domain.userinventory.repository;

import com.star.reward.domain.userinventory.model.entity.UserInventoryBO;
import com.star.reward.domain.userinventory.model.valueobject.InventoryType;

import java.util.List;
import java.util.Optional;

/**
 * 用户库存仓储接口
 */
public interface UserInventoryRepository {
    
    /**
     * 根据库存编号查询
     */
    Optional<UserInventoryBO> findByInventoryNo(String inventoryNo);
    
    /**
     * 根据ID查询
     */
    Optional<UserInventoryBO> findById(Long id);
    
    /**
     * 保存用户库存
     */
    UserInventoryBO save(UserInventoryBO userInventory);
    
    /**
     * 更新用户库存
     */
    UserInventoryBO update(UserInventoryBO userInventory);
    
    /**
     * 删除用户库存（逻辑删除）
     */
    void delete(Long id);
    
    /**
     * 根据所属人ID查询
     */
    List<UserInventoryBO> findByBelongToId(Long belongToId);
    
    /**
     * 根据库存类型查询
     */
    List<UserInventoryBO> findByInventoryType(InventoryType inventoryType);
    
    /**
     * 根据所属人ID和库存类型查询
     */
    List<UserInventoryBO> findByBelongToIdAndType(Long belongToId, InventoryType inventoryType);
    
    /**
     * 根据发布人ID查询
     */
    List<UserInventoryBO> findByPublishById(Long publishById);
}
