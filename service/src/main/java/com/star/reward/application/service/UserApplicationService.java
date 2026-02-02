package com.star.reward.application.service;

import com.star.common.exception.BusinessException;
import com.star.common.page.PageResponse;
import com.star.common.result.ResultCode;
import com.star.reward.domain.product.model.entity.ProductBO;
import com.star.reward.domain.product.repository.ProductRepository;
import com.star.reward.domain.purchaserecord.model.entity.PurchaseRecordBO;
import com.star.reward.domain.purchaserecord.repository.PurchaseRecordRepository;
import com.star.reward.domain.userinventory.model.entity.UserInventoryBO;
import com.star.reward.domain.userinventory.model.valueobject.InventoryType;
import com.star.reward.domain.userinventory.repository.UserInventoryRepository;
import com.star.reward.interfaces.rest.dto.request.ExchangeProductRequest;
import com.star.reward.interfaces.rest.dto.response.ExchangeResponse;
import com.star.reward.interfaces.rest.dto.response.InventoryResponse;
import com.star.reward.interfaces.rest.dto.response.ProductResponse;
import com.star.reward.interfaces.rest.dto.response.UserPointsResponse;
import com.star.reward.shared.context.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 用户应用服务
 */
@Service
@RequiredArgsConstructor
public class UserApplicationService {
    
    private final UserInventoryRepository userInventoryRepository;
    private final ProductRepository productRepository;
    private final PurchaseRecordRepository purchaseRecordRepository;
    
    /**
     * 获取用户积分
     */
    public UserPointsResponse getUserPoints() {
        CurrentUserContext user = CurrentUserContext.get();
        
        List<UserInventoryBO> pointInventories = userInventoryRepository
                .findByBelongToIdAndType(user.getUserId(), InventoryType.POINT);
        
        BigDecimal points = BigDecimal.ZERO;
        if (!pointInventories.isEmpty()) {
            points = pointInventories.get(0).getQuantity();
        }
        
        return UserPointsResponse.builder()
                .points(points)
                .build();
    }
    
    /**
     * 获取用户背包
     */
    public PageResponse<InventoryResponse> getUserInventory() {
        CurrentUserContext user = CurrentUserContext.get();
        
        List<UserInventoryBO> inventories = userInventoryRepository
                .findByBelongToIdAndType(user.getUserId(), InventoryType.PRODUCT);
        
        List<InventoryResponse> responses = inventories.stream()
                .map(this::toInventoryResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(responses, responses.size(), 1, responses.size());
    }
    
    /**
     * 兑换商品
     */
    @Transactional
    public ExchangeResponse exchangeProduct(ExchangeProductRequest request) {
        CurrentUserContext user = CurrentUserContext.get();
        
        // 获取商品信息
        ProductBO product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "商品不存在"));
        
        // 计算兑换数量（默认为最小购买数量）
        BigDecimal quantity = request.getQuantity();
        if (quantity == null) {
            quantity = product.getMinQuantity();
        }
        
        // 计算所需积分
        BigDecimal pointsRequired = product.getPrice().multiply(quantity);
        
        // 获取用户积分
        List<UserInventoryBO> pointInventories = userInventoryRepository
                .findByBelongToIdAndType(user.getUserId(), InventoryType.POINT);
        
        if (pointInventories.isEmpty()) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "积分不足");
        }
        
        UserInventoryBO pointInventory = pointInventories.get(0);
        if (pointInventory.getQuantity().compareTo(pointsRequired) < 0) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "积分不足");
        }
        
        // 扣除积分
        pointInventory.setQuantity(pointInventory.getQuantity().subtract(pointsRequired));
        pointInventory.setUpdateBy(user.getUserNo());
        pointInventory.setUpdateById(user.getUserId());
        pointInventory.setUpdateTime(LocalDateTime.now());
        userInventoryRepository.update(pointInventory);
        
        // 添加商品到背包
        addProductToInventory(user, product, quantity);
        
        // 创建购买记录
        createPurchaseRecord(user, product, quantity, pointsRequired);
        
        return ExchangeResponse.builder()
                .product(toProductResponse(product))
                .quantity(quantity)
                .pointsSpent(pointsRequired)
                .remainingPoints(pointInventory.getQuantity())
                .build();
    }
    
    /**
     * 添加商品到背包
     */
    private void addProductToInventory(CurrentUserContext user, ProductBO product, BigDecimal quantity) {
        // 查找是否已有该商品
        List<UserInventoryBO> existingInventories = userInventoryRepository
                .findByBelongToIdAndType(user.getUserId(), InventoryType.PRODUCT);
        
        UserInventoryBO existingInventory = existingInventories.stream()
                .filter(inv -> product.getName().equals(inv.getName()))
                .findFirst()
                .orElse(null);
        
        if (existingInventory != null) {
            // 更新数量
            existingInventory.setQuantity(existingInventory.getQuantity().add(quantity));
            existingInventory.setUpdateBy(user.getUserNo());
            existingInventory.setUpdateById(user.getUserId());
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
                    .belongTo(user.getUserNo())
                    .belongToId(user.getUserId())
                    .isDeleted(false)
                    .createBy(user.getUserNo())
                    .createById(user.getUserId())
                    .createTime(LocalDateTime.now())
                    .build();
            userInventoryRepository.save(inventory);
        }
    }
    
    /**
     * 创建购买记录
     */
    private void createPurchaseRecord(CurrentUserContext user, ProductBO product, 
                                       BigDecimal quantity, BigDecimal pointsSpent) {
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
                .purchaseBy(user.getUserNo())
                .purchaseById(user.getUserId())
                .isDeleted(false)
                .createBy(user.getUserNo())
                .createById(user.getUserId())
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
     * 转换为库存响应
     */
    private InventoryResponse toInventoryResponse(UserInventoryBO bo) {
        return InventoryResponse.builder()
                .id(bo.getId())
                .inventoryNo(bo.getInventoryNo())
                .name(bo.getName())
                .description(bo.getDescription())
                .quantity(bo.getQuantity())
                .unit(bo.getUnit())
                .createTime(bo.getCreateTime())
                .updateTime(bo.getUpdateTime())
                .build();
    }
    
    /**
     * 转换为商品响应
     */
    private ProductResponse toProductResponse(ProductBO bo) {
        return ProductResponse.builder()
                .id(bo.getId())
                .productNo(bo.getProductNo())
                .name(bo.getName())
                .description(bo.getDescription())
                .price(bo.getPrice())
                .minQuantity(bo.getMinQuantity())
                .unit(bo.getMinUnit())
                .isPreset(bo.getIsPreset())
                .createTime(bo.getCreateTime())
                .updateTime(bo.getUpdateTime())
                .build();
    }
}
