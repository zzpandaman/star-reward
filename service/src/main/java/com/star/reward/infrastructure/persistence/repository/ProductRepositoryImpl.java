package com.star.reward.infrastructure.persistence.repository;

import com.star.reward.domain.product.model.entity.ProductBO;
import com.star.reward.domain.product.repository.ProductRepository;
import com.star.reward.infrastructure.persistence.converter.ProductConverter;
import com.star.reward.infrastructure.persistence.dao.entity.RewardProductDO;
import com.star.reward.infrastructure.persistence.dao.mapper.RewardProductDOMapper;
import com.star.reward.infrastructure.persistence.dao.entity.RewardProductDOExample;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 商品仓储实现
 */
@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    
    private final RewardProductDOMapper mapper;
    private final ProductConverter converter;
    
    @Override
    public Optional<ProductBO> findByProductNo(String productNo) {
        RewardProductDOExample example = new RewardProductDOExample();
        example.createCriteria().andProductNoEqualTo(productNo).andIsDeletedEqualTo((byte) 0);
        
        List<RewardProductDO> list = mapper.selectByExample(example);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(converter.toDomain(list.get(0)));
    }
    
    @Override
    public Optional<ProductBO> findById(Long id) {
        RewardProductDO doEntity = mapper.selectByPrimaryKey(id);
        if (doEntity == null || (doEntity.getIsDeleted() != null && doEntity.getIsDeleted() == 1)) {
            return Optional.empty();
        }
        return Optional.of(converter.toDomain(doEntity));
    }
    
    @Override
    public ProductBO save(ProductBO product) {
        RewardProductDO doEntity = converter.ProductBO2DO(product);
        mapper.insertSelective(doEntity);
        return converter.toDomain(doEntity);
    }
    
    @Override
    public ProductBO update(ProductBO product) {
        RewardProductDO doEntity = converter.ProductBO2DO(product);
        mapper.updateByPrimaryKeySelective(doEntity);
        return converter.toDomain(mapper.selectByPrimaryKey(product.getId()));
    }
    
    @Override
    public void delete(Long id) {
        RewardProductDO doEntity = mapper.selectByPrimaryKey(id);
        if (doEntity != null) {
            doEntity.setIsDeleted((byte) 1);
            mapper.updateByPrimaryKeySelective(doEntity);
        }
    }
    
    @Override
    public List<ProductBO> findAll() {
        RewardProductDOExample example = new RewardProductDOExample();
        example.createCriteria().andIsDeletedEqualTo((byte) 0);
        List<RewardProductDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(converter::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ProductBO> findByIsPreset(Boolean isPreset) {
        RewardProductDOExample example = new RewardProductDOExample();
        example.createCriteria()
                .andIsPresetEqualTo(isPreset != null && isPreset ? (byte) 1 : (byte) 0)
                .andIsDeletedEqualTo((byte) 0);
        List<RewardProductDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(converter::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ProductBO> findByPublishById(Long publishById) {
        RewardProductDOExample example = new RewardProductDOExample();
        example.createCriteria().andPublishByIdEqualTo(publishById).andIsDeletedEqualTo((byte) 0);
        List<RewardProductDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(converter::toDomain)
                .collect(Collectors.toList());
    }
}
