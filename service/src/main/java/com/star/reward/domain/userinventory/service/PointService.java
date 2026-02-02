package com.star.reward.domain.userinventory.service;

import com.star.reward.domain.userinventory.model.entity.UserInventoryBO;
import com.star.reward.domain.userinventory.model.valueobject.InventoryType;
import com.star.reward.domain.userinventory.repository.UserInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 积分领域服务
 */
@Service
@RequiredArgsConstructor
public class PointService {
    
    private final UserInventoryRepository userInventoryRepository;
    
    /**
     * 获取用户积分
     */
    public BigDecimal getUserPoints(Long userId) {
        List<UserInventoryBO> pointInventories = userInventoryRepository
                .findByBelongToIdAndType(userId, InventoryType.POINT);
        
        if (pointInventories.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return pointInventories.get(0).getQuantity();
    }
    
    /**
     * 增加用户积分
     */
    public void addPoints(Long userId, String userNo, BigDecimal amount) {
        List<UserInventoryBO> pointInventories = userInventoryRepository
                .findByBelongToIdAndType(userId, InventoryType.POINT);
        
        if (pointInventories.isEmpty()) {
            // 创建积分库存
            UserInventoryBO inventory = UserInventoryBO.builder()
                    .inventoryNo(generateInventoryNo())
                    .inventoryType(InventoryType.POINT)
                    .name("积分")
                    .description("用户积分")
                    .quantity(amount)
                    .unit("点")
                    .belongTo(userNo)
                    .belongToId(userId)
                    .isDeleted(false)
                    .createBy(userNo)
                    .createById(userId)
                    .createTime(LocalDateTime.now())
                    .build();
            userInventoryRepository.save(inventory);
        } else {
            // 更新积分
            UserInventoryBO inventory = pointInventories.get(0);
            inventory.setQuantity(inventory.getQuantity().add(amount));
            inventory.setUpdateBy(userNo);
            inventory.setUpdateById(userId);
            inventory.setUpdateTime(LocalDateTime.now());
            userInventoryRepository.update(inventory);
        }
    }
    
    /**
     * 扣除用户积分
     * @return 是否扣除成功
     */
    public boolean deductPoints(Long userId, String userNo, BigDecimal amount) {
        List<UserInventoryBO> pointInventories = userInventoryRepository
                .findByBelongToIdAndType(userId, InventoryType.POINT);
        
        if (pointInventories.isEmpty()) {
            return false;
        }
        
        UserInventoryBO inventory = pointInventories.get(0);
        if (inventory.getQuantity().compareTo(amount) < 0) {
            return false;
        }
        
        inventory.setQuantity(inventory.getQuantity().subtract(amount));
        inventory.setUpdateBy(userNo);
        inventory.setUpdateById(userId);
        inventory.setUpdateTime(LocalDateTime.now());
        userInventoryRepository.update(inventory);
        return true;
    }
    
    /**
     * 检查积分是否足够
     */
    public boolean hasEnoughPoints(Long userId, BigDecimal amount) {
        BigDecimal currentPoints = getUserPoints(userId);
        return currentPoints.compareTo(amount) >= 0;
    }
    
    /**
     * 生成库存编号
     */
    private String generateInventoryNo() {
        return "INV" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
