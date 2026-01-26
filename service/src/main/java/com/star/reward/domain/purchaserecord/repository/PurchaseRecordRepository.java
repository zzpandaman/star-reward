package com.star.reward.domain.purchaserecord.repository;

import com.star.reward.domain.purchaserecord.model.entity.PurchaseRecordBO;

import java.util.List;
import java.util.Optional;

/**
 * 购买记录仓储接口
 */
public interface PurchaseRecordRepository {
    
    /**
     * 根据购买记录编号查询
     */
    Optional<PurchaseRecordBO> findByPurchaseNo(String purchaseNo);
    
    /**
     * 根据ID查询
     */
    Optional<PurchaseRecordBO> findById(Long id);
    
    /**
     * 保存购买记录
     */
    PurchaseRecordBO save(PurchaseRecordBO purchaseRecord);
    
    /**
     * 更新购买记录
     */
    PurchaseRecordBO update(PurchaseRecordBO purchaseRecord);
    
    /**
     * 删除购买记录（逻辑删除）
     */
    void delete(Long id);
    
    /**
     * 根据商品编号查询
     */
    List<PurchaseRecordBO> findByProductNo(String productNo);
    
    /**
     * 根据购买人ID查询
     */
    List<PurchaseRecordBO> findByPurchaseById(Long purchaseById);
    
    /**
     * 根据发布人ID查询
     */
    List<PurchaseRecordBO> findByPublishById(Long publishById);
}
