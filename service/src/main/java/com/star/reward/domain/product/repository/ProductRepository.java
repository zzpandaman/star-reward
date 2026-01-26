package com.star.reward.domain.product.repository;

import com.star.reward.domain.product.model.entity.ProductBO;

import java.util.List;
import java.util.Optional;

/**
 * 商品仓储接口
 */
public interface ProductRepository {
    
    /**
     * 根据商品编号查询
     */
    Optional<ProductBO> findByProductNo(String productNo);
    
    /**
     * 根据ID查询
     */
    Optional<ProductBO> findById(Long id);
    
    /**
     * 保存商品
     */
    ProductBO save(ProductBO product);
    
    /**
     * 更新商品
     */
    ProductBO update(ProductBO product);
    
    /**
     * 删除商品（逻辑删除）
     */
    void delete(Long id);
    
    /**
     * 查询所有未删除的商品
     */
    List<ProductBO> findAll();
    
    /**
     * 根据是否为预设查询
     */
    List<ProductBO> findByIsPreset(Boolean isPreset);
    
    /**
     * 根据发布人ID查询
     */
    List<ProductBO> findByPublishById(Long publishById);
}
