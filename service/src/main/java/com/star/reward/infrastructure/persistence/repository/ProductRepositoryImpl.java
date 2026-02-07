package com.star.reward.infrastructure.persistence.repository;

import com.star.reward.domain.product.model.entity.ProductBO;
import com.star.reward.domain.product.model.query.ProductQueryParam;
import com.star.reward.domain.product.repository.ProductRepository;
import com.star.reward.infrastructure.persistence.converter.ProductConverter;
import com.star.reward.infrastructure.persistence.dao.entity.RewardProductDO;
import com.star.reward.infrastructure.persistence.dao.entity.RewardProductDOExample;
import com.star.reward.infrastructure.persistence.dao.mapper.RewardProductDOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

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

    @Override
    public Optional<ProductBO> findByProductNo(String productNo) {
        List<ProductBO> list = list(ProductQueryParam.builder().productNo(productNo).build());
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public Optional<ProductBO> findById(Long id) {
        RewardProductDO doEntity = mapper.selectByPrimaryKey(id);
        if (doEntity == null || (doEntity.getIsDeleted() != null && doEntity.getIsDeleted() == 1)) {
            return Optional.empty();
        }
        return Optional.of(ProductConverter.doToEntity(doEntity));
    }

    @Override
    public ProductBO save(ProductBO product) {
        RewardProductDO doEntity = ProductConverter.entityToDo(product);
        mapper.insertSelective(doEntity);
        return ProductConverter.doToEntity(doEntity);
    }

    @Override
    public ProductBO update(ProductBO product) {
        RewardProductDO doEntity = ProductConverter.partialEntityToDo(product);
        mapper.updateByPrimaryKeySelective(doEntity);
        return ProductConverter.doToEntity(mapper.selectByPrimaryKey(product.getId()));
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
        return list(ProductQueryParam.builder().build());
    }

    @Override
    public List<ProductBO> findByIsPreset(Boolean isPreset) {
        return list(ProductQueryParam.builder().isPreset(isPreset).build());
    }

    @Override
    public List<ProductBO> findByPublishById(Long publishById) {
        return list(ProductQueryParam.builder().publishById(publishById).build());
    }

    @Override
    public List<ProductBO> listByQuery(ProductQueryParam param) {
        return list(param);
    }

    @Override
    public long countByQuery(ProductQueryParam param) {
        return mapper.countByExample(buildExampleForCount(param));
    }

    private List<ProductBO> list(ProductQueryParam param) {
        RewardProductDOExample example = buildExample(param);
        return mapper.selectByExample(example).stream()
                .map(ProductConverter::doToEntity)
                .collect(Collectors.toList());
    }

    private static RewardProductDOExample buildExample(ProductQueryParam param) {
        RewardProductDOExample example = new RewardProductDOExample();
        RewardProductDOExample.Criteria c = example.createCriteria();
        if (param != null) {
            if (StringUtils.hasText(param.getProductNo())) {
                c.andProductNoEqualTo(param.getProductNo());
            }
            if (param.getIsPreset() != null) {
                c.andIsPresetEqualTo(param.getIsPreset() ? (byte) 1 : (byte) 0);
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

    /** count 不使用 LIMIT */
    private static RewardProductDOExample buildExampleForCount(ProductQueryParam param) {
        RewardProductDOExample example = new RewardProductDOExample();
        RewardProductDOExample.Criteria c = example.createCriteria();
        if (param != null) {
            if (StringUtils.hasText(param.getProductNo())) {
                c.andProductNoEqualTo(param.getProductNo());
            }
            if (param.getIsPreset() != null) {
                c.andIsPresetEqualTo(param.getIsPreset() ? (byte) 1 : (byte) 0);
            }
            if (param.getPublishById() != null) {
                c.andPublishByIdEqualTo(param.getPublishById());
            }
            c.andIsDeletedEqualTo(param.getIsDeleted() != null ? param.getIsDeleted() : (byte) 0);
        } else {
            c.andIsDeletedEqualTo((byte) 0);
        }
        return example;
    }
}
