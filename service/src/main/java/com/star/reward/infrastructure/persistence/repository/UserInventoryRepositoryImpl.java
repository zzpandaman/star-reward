package com.star.reward.infrastructure.persistence.repository;

import com.star.reward.domain.userinventory.model.entity.UserInventoryBO;
import com.star.reward.domain.userinventory.model.query.UserInventoryQueryParam;
import com.star.reward.domain.userinventory.model.valueobject.InventoryType;
import com.star.reward.domain.userinventory.repository.UserInventoryRepository;
import com.star.reward.infrastructure.persistence.converter.UserInventoryConverter;
import com.star.reward.infrastructure.persistence.dao.entity.RewardUserInventoryDO;
import com.star.reward.infrastructure.persistence.dao.entity.RewardUserInventoryDOExample;
import com.star.reward.infrastructure.persistence.dao.mapper.RewardUserInventoryDOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户库存仓储实现
 */
@Repository
@RequiredArgsConstructor
public class UserInventoryRepositoryImpl implements UserInventoryRepository {

    private final RewardUserInventoryDOMapper mapper;

    @Override
    public Optional<UserInventoryBO> findByInventoryNo(String inventoryNo) {
        List<UserInventoryBO> list = list(UserInventoryQueryParam.builder().inventoryNo(inventoryNo).build());
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public Optional<UserInventoryBO> findById(Long id) {
        RewardUserInventoryDO doEntity = mapper.selectByPrimaryKey(id);
        if (doEntity == null || (doEntity.getIsDeleted() != null && doEntity.getIsDeleted() == 1)) {
            return Optional.empty();
        }
        return Optional.of(UserInventoryConverter.doToEntity(doEntity));
    }

    @Override
    public UserInventoryBO save(UserInventoryBO userInventory) {
        RewardUserInventoryDO doEntity = UserInventoryConverter.entityToDo(userInventory);
        mapper.insertSelective(doEntity);
        return UserInventoryConverter.doToEntity(doEntity);
    }

    @Override
    public UserInventoryBO update(UserInventoryBO userInventory) {
        RewardUserInventoryDO doEntity = UserInventoryConverter.entityToDo(userInventory);
        mapper.updateByPrimaryKeySelective(doEntity);
        return UserInventoryConverter.doToEntity(mapper.selectByPrimaryKey(userInventory.getId()));
    }

    @Override
    public void delete(Long id) {
        RewardUserInventoryDO doEntity = mapper.selectByPrimaryKey(id);
        if (doEntity != null) {
            doEntity.setIsDeleted((byte) 1);
            mapper.updateByPrimaryKeySelective(doEntity);
        }
    }

    @Override
    public List<UserInventoryBO> findByBelongToId(Long belongToId) {
        return list(UserInventoryQueryParam.builder().belongToId(belongToId).build());
    }

    @Override
    public List<UserInventoryBO> findByInventoryType(InventoryType inventoryType) {
        return list(UserInventoryQueryParam.builder()
                .inventoryType(inventoryType != null ? inventoryType.getCode() : null)
                .build());
    }

    @Override
    public List<UserInventoryBO> findByBelongToIdAndType(Long belongToId, InventoryType inventoryType) {
        return list(UserInventoryQueryParam.builder()
                .belongToId(belongToId)
                .inventoryType(inventoryType != null ? inventoryType.getCode() : null)
                .build());
    }

    @Override
    public List<UserInventoryBO> findByPublishById(Long publishById) {
        return list(UserInventoryQueryParam.builder().publishById(publishById).build());
    }

    private List<UserInventoryBO> list(UserInventoryQueryParam param) {
        RewardUserInventoryDOExample example = buildExample(param);
        return mapper.selectByExample(example).stream()
                .map(UserInventoryConverter::doToEntity)
                .collect(Collectors.toList());
    }

    private static RewardUserInventoryDOExample buildExample(UserInventoryQueryParam param) {
        RewardUserInventoryDOExample example = new RewardUserInventoryDOExample();
        RewardUserInventoryDOExample.Criteria c = example.createCriteria();
        if (param != null) {
            if (StringUtils.hasText(param.getInventoryNo())) {
                c.andInventoryNoEqualTo(param.getInventoryNo());
            }
            if (param.getBelongToId() != null) {
                c.andBelongToIdEqualTo(param.getBelongToId());
            }
            if (StringUtils.hasText(param.getInventoryType())) {
                c.andInventoryTypeEqualTo(param.getInventoryType());
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
