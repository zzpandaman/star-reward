package com.star.reward.infrastructure.persistence.repository;

import com.star.reward.domain.purchaserecord.model.entity.PurchaseRecordBO;
import com.star.reward.domain.purchaserecord.model.query.PurchaseRecordQueryParam;
import com.star.reward.domain.purchaserecord.repository.PurchaseRecordRepository;
import com.star.reward.infrastructure.persistence.converter.PurchaseRecordConverter;
import com.star.reward.infrastructure.persistence.dao.entity.RewardPurchaseRecordDO;
import com.star.reward.infrastructure.persistence.dao.entity.RewardPurchaseRecordDOExample;
import com.star.reward.infrastructure.persistence.dao.mapper.RewardPurchaseRecordDOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

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

    @Override
    public Optional<PurchaseRecordBO> findByPurchaseNo(String purchaseNo) {
        List<PurchaseRecordBO> list = list(PurchaseRecordQueryParam.builder().purchaseNo(purchaseNo).build());
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public Optional<PurchaseRecordBO> findById(Long id) {
        RewardPurchaseRecordDO doEntity = mapper.selectByPrimaryKey(id);
        if (doEntity == null || (doEntity.getIsDeleted() != null && doEntity.getIsDeleted() == 1)) {
            return Optional.empty();
        }
        return Optional.of(PurchaseRecordConverter.doToEntity(doEntity));
    }

    @Override
    public PurchaseRecordBO save(PurchaseRecordBO purchaseRecord) {
        RewardPurchaseRecordDO doEntity = PurchaseRecordConverter.entityToDo(purchaseRecord);
        mapper.insertSelective(doEntity);
        return PurchaseRecordConverter.doToEntity(doEntity);
    }

    @Override
    public PurchaseRecordBO update(PurchaseRecordBO purchaseRecord) {
        RewardPurchaseRecordDO doEntity = PurchaseRecordConverter.entityToDo(purchaseRecord);
        mapper.updateByPrimaryKeySelective(doEntity);
        return PurchaseRecordConverter.doToEntity(mapper.selectByPrimaryKey(purchaseRecord.getId()));
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
        return list(PurchaseRecordQueryParam.builder().productNo(productNo).build());
    }

    @Override
    public List<PurchaseRecordBO> findByPurchaseById(Long purchaseById) {
        return list(PurchaseRecordQueryParam.builder().purchaseById(purchaseById).build());
    }

    @Override
    public List<PurchaseRecordBO> findByPublishById(Long publishById) {
        return list(PurchaseRecordQueryParam.builder().publishById(publishById).build());
    }

    @Override
    public boolean existsByProductNo(String productNo) {
        return mapper.countByExample(buildExample(
                PurchaseRecordQueryParam.builder().productNo(productNo).build())) > 0;
    }

    private List<PurchaseRecordBO> list(PurchaseRecordQueryParam param) {
        RewardPurchaseRecordDOExample example = buildExample(param);
        return mapper.selectByExample(example).stream()
                .map(PurchaseRecordConverter::doToEntity)
                .collect(Collectors.toList());
    }

    private static RewardPurchaseRecordDOExample buildExample(PurchaseRecordQueryParam param) {
        RewardPurchaseRecordDOExample example = new RewardPurchaseRecordDOExample();
        RewardPurchaseRecordDOExample.Criteria c = example.createCriteria();
        if (param != null) {
            if (StringUtils.hasText(param.getPurchaseNo())) {
                c.andPurchaseNoEqualTo(param.getPurchaseNo());
            }
            if (StringUtils.hasText(param.getProductNo())) {
                c.andProductNoEqualTo(param.getProductNo());
            }
            if (param.getPurchaseById() != null) {
                c.andPurchaseByIdEqualTo(param.getPurchaseById());
            }
            if (param.getPublishById() != null) {
                c.andPublishByIdEqualTo(param.getPublishById());
            }
            c.andIsDeletedEqualTo(param.getIsDeleted() != null ? param.getIsDeleted() : (byte) 0);
        } else {
            c.andIsDeletedEqualTo((byte) 0);
        }
        if (param != null && StringUtils.hasText(param.getOrderBy())) {
            example.setOrderByClause(param.getOrderBy());
        }
        if (param != null && param.hasPagination()) {
            example.page(param.getPage(), param.getPageSize());
        }
        return example;
    }
}
