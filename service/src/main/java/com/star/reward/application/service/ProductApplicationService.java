package com.star.reward.application.service;

import com.star.common.exception.BusinessException;
import com.star.common.page.PageResponse;
import com.star.common.result.ResultCode;
import com.star.reward.domain.product.model.entity.ProductBO;
import com.star.reward.domain.product.repository.ProductRepository;
import com.star.reward.domain.purchaserecord.repository.PurchaseRecordRepository;
import com.star.reward.domain.userinventory.model.entity.UserInventoryBO;
import com.star.reward.domain.userinventory.model.valueobject.InventoryType;
import com.star.reward.domain.userinventory.repository.UserInventoryRepository;
import com.star.reward.interfaces.rest.dto.request.CreateProductRequest;
import com.star.reward.interfaces.rest.dto.request.UpdateProductRequest;
import com.star.reward.interfaces.rest.dto.response.ProductResponse;
import com.star.reward.shared.context.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 商品应用服务
 */
@Service
@RequiredArgsConstructor
public class ProductApplicationService {
    
    private final ProductRepository productRepository;
    private final UserInventoryRepository userInventoryRepository;
    private final PurchaseRecordRepository purchaseRecordRepository;
    
    /**
     * 获取所有商品
     */
    public PageResponse<ProductResponse> getAllProducts() {
        List<ProductBO> products = productRepository.findAll();
        List<ProductResponse> responses = products.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return PageResponse.of(responses, responses.size(), 1, responses.size());
    }
    
    /**
     * 根据ID获取商品
     */
    public ProductResponse getProductById(Long id) {
        ProductBO product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "商品不存在"));
        return toResponse(product);
    }
    
    /**
     * 创建商品
     */
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        CurrentUserContext user = CurrentUserContext.get();
        
        ProductBO product = ProductBO.builder()
                .productNo(generateProductNo())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .minQuantity(request.getMinQuantity())
                .minUnit(request.getUnit())
                .isPreset(false)
                .isDeleted(false)
                .publishBy(user.getUserNo())
                .publishById(user.getUserId())
                .createBy(user.getUserNo())
                .createById(user.getUserId())
                .createTime(LocalDateTime.now())
                .build();
        
        ProductBO saved = productRepository.save(product);
        return toResponse(saved);
    }
    
    /**
     * 更新商品
     */
    @Transactional
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        ProductBO product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "商品不存在"));
        
        if (Boolean.TRUE.equals(product.getIsPreset())) {
            throw new BusinessException(ResultCode.CANNOT_UPDATE_PRESET.getCode(), 
                    ResultCode.CANNOT_UPDATE_PRESET.getMessage());
        }
        
        CurrentUserContext user = CurrentUserContext.get();
        
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getMinQuantity() != null) {
            product.setMinQuantity(request.getMinQuantity());
        }
        if (request.getUnit() != null) {
            product.setMinUnit(request.getUnit());
        }
        product.setUpdateBy(user.getUserNo());
        product.setUpdateById(user.getUserId());
        product.setUpdateTime(LocalDateTime.now());
        
        ProductBO updated = productRepository.update(product);
        return toResponse(updated);
    }
    
    /**
     * 删除商品
     */
    @Transactional
    public void deleteProduct(Long id) {
        ProductBO product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "商品不存在"));
        
        if (Boolean.TRUE.equals(product.getIsPreset())) {
            throw new BusinessException(ResultCode.CANNOT_UPDATE_PRESET.getCode(), "不能删除预设商品");
        }
        
        // 检查是否有购买记录
        boolean hasRecords = purchaseRecordRepository.existsByProductNo(product.getProductNo());
        if (hasRecords) {
            throw new BusinessException(ResultCode.CANNOT_DELETE_WITH_RECORDS.getCode(), 
                    "不能删除有兑换记录的商品");
        }
        
        // 检查用户背包中是否存在该商品
        List<UserInventoryBO> inventories = userInventoryRepository.findByPublishById(product.getPublishById());
        boolean existsInInventory = inventories.stream()
                .anyMatch(inv -> inv.getInventoryType() == InventoryType.PRODUCT 
                        && inv.getName().equals(product.getName()));
        if (existsInInventory) {
            throw new BusinessException(ResultCode.CANNOT_DELETE_WITH_RECORDS.getCode(), 
                    "不能删除背包中存在的商品");
        }
        
        productRepository.delete(id);
    }
    
    /**
     * 生成商品编号
     */
    private String generateProductNo() {
        return "PRD" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
    
    /**
     * 转换为响应
     */
    private ProductResponse toResponse(ProductBO bo) {
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
