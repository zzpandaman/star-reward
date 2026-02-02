package com.star.reward.domain.userinventory.service;

import com.star.common.exception.BusinessException;
import com.star.common.result.ResultCode;
import com.star.reward.domain.product.model.entity.ProductBO;
import com.star.reward.domain.product.repository.ProductRepository;
import com.star.reward.domain.purchaserecord.model.entity.PurchaseRecordBO;
import com.star.reward.domain.purchaserecord.repository.PurchaseRecordRepository;
import com.star.reward.domain.userinventory.model.entity.UserInventoryBO;
import com.star.reward.domain.userinventory.model.valueobject.InventoryType;
import com.star.reward.domain.userinventory.repository.UserInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 兑换领域服务
 */
@Service
@RequiredArgsConstructor
public class ExchangeService {
    
    private final PointService pointService;
    private final ProductRepository productRepository;
    private final UserInventoryRepository userInventoryRepository;
    private final PurchaseRecordRepository purchaseRecordRepository;
    
    /**
     * 兑换商品
     */
    @Transactional
    public ExchangeResult exchange(Long userId, String userNo, Long productId, BigDecimal quantity) {
        // 获取商品信息
        ProductBO product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "商品不存在"));
        
        // 计算兑换数量（默认为最小购买数量）
        if (quantity == null) {
            quantity = product.getMinQuantity();
        }
        
        // 计算所需积分
        BigDecimal pointsRequired = product.getPrice().multiply(quantity);
        
        // 检查并扣除积分
        if (!pointService.deductPoints(userId, userNo, pointsRequired)) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "积分不足");
        }
        
        // 添加商品到背包
        addProductToInventory(userId, userNo, product, quantity);
        
        // 创建购买记录
        createPurchaseRecord(userId, userNo, product, quantity);
        
        // 获取剩余积分
        BigDecimal remainingPoints = pointService.getUserPoints(userId);
        
        return ExchangeResult.builder()
                .product(product)
                .quantity(quantity)
                .pointsSpent(pointsRequired)
                .remainingPoints(remainingPoints)
                .build();
    }
    
    /**
     * 添加商品到背包
     */
    private void addProductToInventory(Long userId, String userNo, ProductBO product, BigDecimal quantity) {
        // 查找是否已有该商品
        List<UserInventoryBO> existingInventories = userInventoryRepository
                .findByBelongToIdAndType(userId, InventoryType.PRODUCT);
        
        UserInventoryBO existingInventory = existingInventories.stream()
                .filter(inv -> product.getName().equals(inv.getName()))
                .findFirst()
                .orElse(null);
        
        if (existingInventory != null) {
            // 更新数量
            existingInventory.setQuantity(existingInventory.getQuantity().add(quantity));
            existingInventory.setUpdateBy(userNo);
            existingInventory.setUpdateById(userId);
            existingInventory.setUpdateTime(LocalDateTime.now());
            userInventoryRepository.update(existingInventory);
        } else {
            // 创建新库存
            UserInventoryBO inventory = UserInventoryBO.builder()
                    .inventoryNo(generateInventoryNo())
                    .inventoryType(InventoryType.PRODUCT)
                    .name(product.getName())
                    .description(product.getDescription())
                    .quantity(quantity)
                    .unit(product.getMinUnit())
                    .publishBy(product.getPublishBy())
                    .publishById(product.getPublishById())
                    .belongTo(userNo)
                    .belongToId(userId)
                    .isDeleted(false)
                    .createBy(userNo)
                    .createById(userId)
                    .createTime(LocalDateTime.now())
                    .build();
            userInventoryRepository.save(inventory);
        }
    }
    
    /**
     * 创建购买记录
     */
    private void createPurchaseRecord(Long userId, String userNo, ProductBO product, BigDecimal quantity) {
        PurchaseRecordBO record = PurchaseRecordBO.builder()
                .purchaseNo(generateRecordNo())
                .productNo(product.getProductNo())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .minQuantity(product.getMinQuantity())
                .minUnit(product.getMinUnit())
                .publishBy(product.getPublishBy())
                .publishById(product.getPublishById())
                .purchaseQuantity(quantity.intValue())
                .purchaseBy(userNo)
                .purchaseById(userId)
                .isDeleted(false)
                .createBy(userNo)
                .createById(userId)
                .createTime(LocalDateTime.now())
                .build();
        purchaseRecordRepository.save(record);
    }
    
    /**
     * 生成库存编号
     */
    private String generateInventoryNo() {
        return "INV" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
    
    /**
     * 生成记录编号
     */
    private String generateRecordNo() {
        return "REC" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
    
    /**
     * 兑换结果
     */
    @lombok.Data
    @lombok.Builder
    public static class ExchangeResult {
        private ProductBO product;
        private BigDecimal quantity;
        private BigDecimal pointsSpent;
        private BigDecimal remainingPoints;
    }
}
