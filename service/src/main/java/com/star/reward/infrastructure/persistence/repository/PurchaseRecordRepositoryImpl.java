package com.star.reward.infrastructure.persistence.repository;

import com.star.reward.domain.purchaserecord.model.entity.PurchaseRecordBO;
import com.star.reward.domain.purchaserecord.repository.PurchaseRecordRepository;
import com.star.reward.infrastructure.persistence.converter.PurchaseRecordConverter;
import com.star.reward.infrastructure.persistence.dao.entity.RewardPurchaseRecordDO;
import com.star.reward.infrastructure.persistence.dao.entity.RewardPurchaseRecordDOExample;
import com.star.reward.infrastructure.persistence.dao.mapper.RewardPurchaseRecordDOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 购买记录仓储实现
 */
@Repository
@RequiredArgsConstructor
public class PurchaseRecordRepositoryImpl implements PurchaseRecordRepository {
    
    private final RewardPurchaseRecordDOMapper mapper;
    private final PurchaseRecordConverter converter;
    
    @Override
    public Optional<PurchaseRecordBO> findByPurchaseNo(String purchaseNo) {
        RewardPurchaseRecordDOExample example = new RewardPurchaseRecordDOExample();
        example.createCriteria().andPurchaseNoEqualTo(purchaseNo).andIsDeletedEqualTo((byte) 0);
        
        List<RewardPurchaseRecordDO> list = mapper.selectByExample(example);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(converter.toDomain(list.get(0)));
    }
    
    @Override
    public Optional<PurchaseRecordBO> findById(Long id) {
        RewardPurchaseRecordDO doEntity = mapper.selectByPrimaryKey(id);
        if (doEntity == null || (doEntity.getIsDeleted() != null && doEntity.getIsDeleted() == 1)) {
            return Optional.empty();
        }
        return Optional.of(converter.toDomain(doEntity));
    }
    
    @Override
    public PurchaseRecordBO save(PurchaseRecordBO purchaseRecord) {
        RewardPurchaseRecordDO doEntity = converter.PurchaseRecordBO2DO(purchaseRecord);
        mapper.insertSelective(doEntity);
        return converter.toDomain(doEntity);
    }
    
    @Override
    public PurchaseRecordBO update(PurchaseRecordBO purchaseRecord) {
        RewardPurchaseRecordDO doEntity = converter.PurchaseRecordBO2DO(purchaseRecord);
        mapper.updateByPrimaryKeySelective(doEntity);
        return converter.toDomain(mapper.selectByPrimaryKey(purchaseRecord.getId()));
    }
    
    @Override
    public void delete(Long id) {
        RewardPurchaseRecordDO doEntity = mapper.selectByPrimaryKey(id);
        if (doEntity != null) {
            doEntity.setIsDeleted((byte) 1);
            mapper.updateByPrimaryKeySelective(doEntity);
        }
    }
    
    @Override
    public List<PurchaseRecordBO> findByProductNo(String productNo) {
        RewardPurchaseRecordDOExample example = new RewardPurchaseRecordDOExample();
        example.createCriteria().andProductNoEqualTo(productNo).andIsDeletedEqualTo((byte) 0);
        List<RewardPurchaseRecordDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(converter::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PurchaseRecordBO> findByPurchaseById(Long purchaseById) {
        RewardPurchaseRecordDOExample example = new RewardPurchaseRecordDOExample();
        example.createCriteria().andPurchaseByIdEqualTo(purchaseById).andIsDeletedEqualTo((byte) 0);
        List<RewardPurchaseRecordDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(converter::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PurchaseRecordBO> findByPublishById(Long publishById) {
        RewardPurchaseRecordDOExample example = new RewardPurchaseRecordDOExample();
        example.createCriteria().andPublishByIdEqualTo(publishById).andIsDeletedEqualTo((byte) 0);
        List<RewardPurchaseRecordDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(converter::toDomain)
                .collect(Collectors.toList());
    }
}
