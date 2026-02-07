package com.star.reward.domain.purchaserecord.service;

import com.star.common.exception.BusinessException;
import com.star.common.result.ResultCode;
import com.star.reward.domain.pointrecord.model.constant.PointRecordConstants;
import com.star.reward.domain.pointrecord.model.entity.PointRecordBO;
import com.star.reward.domain.pointrecord.repository.PointRecordRepository;
import com.star.reward.domain.product.model.entity.ProductBO;
import com.star.reward.domain.product.repository.ProductRepository;
import com.star.reward.domain.purchaserecord.model.constant.PurchaseRecordConstants;
import com.star.reward.domain.purchaserecord.model.entity.PurchaseRecordBO;
import com.star.reward.domain.purchaserecord.repository.PurchaseRecordRepository;
import com.star.reward.domain.shared.util.RewardNoGenerator;
import com.star.reward.domain.userinventory.model.constant.UserInventoryConstants;
import com.star.reward.domain.userinventory.model.entity.UserInventoryBO;
import com.star.reward.domain.userinventory.model.valueobject.InventoryType;
import com.star.reward.domain.userinventory.repository.UserInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 兑换领域服务
 */
@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final ProductRepository productRepository;
    private final UserInventoryRepository userInventoryRepository;
    private final PurchaseRecordRepository purchaseRecordRepository;
    private final PointRecordRepository pointRecordRepository;

    /**
     * 兑换商品
     *
     * @param userId 用户ID
     * @param userNo 用户账号
     * @param productId 商品ID
     * @param quantity 兑换数量（可为空，默认最小购买数量）
     * @return 兑换结果
     */
    public ExchangeResult exchange(Long userId, String userNo, Long productId, BigDecimal quantity) {
        ProductBO product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND.getCode(), "商品不存在"));

        if (quantity == null) {
            quantity = product.getMinQuantity();
        }

        BigDecimal pointsRequired = product.getPrice().multiply(quantity);
        String purchaseNo = RewardNoGenerator.generate(PurchaseRecordConstants.PURCHASE_NO_PREFIX);
        LocalDateTime now = LocalDateTime.now();

        List<UserInventoryBO> pointInventories = userInventoryRepository
                .findByBelongToIdAndType(userId, InventoryType.POINT);

        if (pointInventories.isEmpty()) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "积分不足");
        }

        UserInventoryBO pointInventory = pointInventories.get(0);
        if (pointInventory.getQuantity().compareTo(pointsRequired) < 0) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR.getCode(), "积分不足");
        }

        pointInventory.setQuantity(pointInventory.getQuantity().subtract(pointsRequired));
        pointInventory.setUpdateBy(userNo);
        pointInventory.setUpdateById(userId);
        pointInventory.setUpdateTime(now);
        userInventoryRepository.update(pointInventory);

        PointRecordBO spendRecord = PointRecordBO.createSpend(
                RewardNoGenerator.generate(PointRecordConstants.RECORD_NO_PREFIX),
                pointsRequired, userNo, userId, purchaseNo, "兑换商品: " + product.getName(), now);
        pointRecordRepository.save(spendRecord);

        addProductToInventory(userId, userNo, product, quantity);
        createPurchaseRecord(userId, userNo, product, quantity, purchaseNo, now);

        return ExchangeResult.builder()
                .product(product)
                .quantity(quantity)
                .pointsSpent(pointsRequired)
                .remainingPoints(pointInventory.getQuantity())
                .build();
    }

    private void addProductToInventory(Long userId, String userNo, ProductBO product, BigDecimal quantity) {
        List<UserInventoryBO> existingInventories = userInventoryRepository
                .findByBelongToIdAndType(userId, InventoryType.PRODUCT);

        UserInventoryBO existingInventory = existingInventories.stream()
                .filter(inv -> product.getName().equals(inv.getName()))
                .findFirst()
                .orElse(null);

        if (existingInventory != null) {
            existingInventory.setQuantity(existingInventory.getQuantity().add(quantity));
            existingInventory.setUpdateBy(userNo);
            existingInventory.setUpdateById(userId);
            existingInventory.setUpdateTime(LocalDateTime.now());
            userInventoryRepository.update(existingInventory);
        } else {
            UserInventoryBO inventory = UserInventoryBO.createProductInventory(
                    RewardNoGenerator.generate(UserInventoryConstants.INVENTORY_NO_PREFIX),
                    product.getName(), product.getDescription(), quantity, product.getMinUnit(),
                    product.getPublishBy(), product.getPublishById(), userNo, userId, LocalDateTime.now());
            userInventoryRepository.save(inventory);
        }
    }

    private void createPurchaseRecord(Long userId, String userNo, ProductBO product, BigDecimal quantity,
            String purchaseNo, LocalDateTime now) {
        PurchaseRecordBO record = PurchaseRecordBO.createFromExchange(
                product, quantity, userNo, userId, purchaseNo, now);
        purchaseRecordRepository.save(record);
    }

    /**
     * 兑换结果（领域层）
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
