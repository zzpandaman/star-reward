package com.star.reward.application.service;

import com.star.common.exception.BusinessException;
import com.star.common.page.PageResponse;
import com.star.common.result.ResultCode;
import com.star.reward.application.assembler.ProductAssembler;
import com.star.reward.application.command.CreateProductCommand;
import com.star.reward.application.command.ProductQueryCommand;
import com.star.reward.domain.product.model.constant.ProductConstants;
import com.star.reward.domain.product.model.query.ProductQueryParam;
import com.star.reward.domain.product.model.entity.ProductBO;
import com.star.reward.domain.product.repository.ProductRepository;
import com.star.reward.domain.purchaserecord.repository.PurchaseRecordRepository;
import com.star.reward.domain.userinventory.model.entity.UserInventoryBO;
import com.star.reward.domain.userinventory.model.valueobject.InventoryType;
import com.star.reward.domain.userinventory.repository.UserInventoryRepository;
import com.star.reward.application.command.UpdateProductCommand;
import com.star.reward.interfaces.rest.dto.response.ProductResponse;
import com.star.reward.shared.context.CurrentUserContext;
import com.star.reward.domain.shared.util.RewardNoGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
     * 分页查询商品
     */
    public PageResponse<ProductResponse> getAllProducts(ProductQueryCommand command) {
        ProductQueryParam param = ProductAssembler.commandToQueryParam(command);
        param.setPage(command != null ? command.getPage() : 1);
        param.setPageSize(command != null ? command.getPageSize() : 10);
        List<ProductBO> products = productRepository.listByQuery(param);
        long total = productRepository.countByQuery(param);
        List<ProductResponse> responses = products.stream()
                .map(ProductAssembler::entityToResponse)
                .collect(Collectors.toList());
        return PageResponse.of(responses, total, param);
    }
    
    /**
     * 根据ID获取商品
     */
    public ProductResponse getProductById(Long id) {
        ProductBO product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "商品不存在"));
        return ProductAssembler.entityToResponse(product);
    }

    /**
     * 创建商品
     */
    @Transactional
    public ProductResponse createProduct(CreateProductCommand command) {
        CurrentUserContext user = CurrentUserContext.get();

        ProductBO product = ProductAssembler.createCommandToEntity(command);
        product.initForCreate(
                RewardNoGenerator.generate(ProductConstants.PRODUCT_NO_PREFIX),
                user.getUserNo(), user.getUserId(), LocalDateTime.now());

        ProductBO saved = productRepository.save(product);
        return ProductAssembler.entityToResponse(saved);
    }
    
    /**
     * 更新商品
     */
    @Transactional
    public ProductResponse updateProduct(Long id, UpdateProductCommand command) {
        ProductBO product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "商品不存在"));
        
        if (Boolean.TRUE.equals(product.getIsPreset())) {
            throw new BusinessException(ResultCode.CANNOT_UPDATE_PRESET.getCode(),
                    ResultCode.CANNOT_UPDATE_PRESET.getMessage());
        }

        CurrentUserContext user = CurrentUserContext.get();
        ProductBO patch = ProductAssembler.updateCommandToPartialEntity(id, command,
                user.getUserNo(), user.getUserId(), LocalDateTime.now());
        ProductBO updated = productRepository.update(patch);
        return ProductAssembler.entityToResponse(updated);
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
}
