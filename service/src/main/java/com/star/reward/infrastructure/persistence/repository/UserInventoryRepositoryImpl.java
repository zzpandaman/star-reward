package com.star.reward.infrastructure.persistence.repository;

import com.star.reward.domain.userinventory.model.entity.UserInventoryBO;
import com.star.reward.domain.userinventory.model.valueobject.InventoryType;
import com.star.reward.domain.userinventory.repository.UserInventoryRepository;
import com.star.reward.infrastructure.persistence.converter.UserInventoryConverter;
import com.star.reward.infrastructure.persistence.dao.entity.RewardUserInventoryDO;
import com.star.reward.infrastructure.persistence.dao.entity.RewardUserInventoryDOExample;
import com.star.reward.infrastructure.persistence.dao.mapper.RewardUserInventoryDOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
        RewardUserInventoryDOExample example = new RewardUserInventoryDOExample();
        example.createCriteria().andInventoryNoEqualTo(inventoryNo).andIsDeletedEqualTo((byte) 0);
        
        List<RewardUserInventoryDO> list = mapper.selectByExample(example);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(UserInventoryConverter.doToEntity(list.get(0)));
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
        RewardUserInventoryDOExample example = new RewardUserInventoryDOExample();
        example.createCriteria().andBelongToIdEqualTo(belongToId).andIsDeletedEqualTo((byte) 0);
        List<RewardUserInventoryDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(UserInventoryConverter::doToEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<UserInventoryBO> findByInventoryType(InventoryType inventoryType) {
        RewardUserInventoryDOExample example = new RewardUserInventoryDOExample();
        example.createCriteria().andInventoryTypeEqualTo(inventoryType.getCode()).andIsDeletedEqualTo((byte) 0);
        List<RewardUserInventoryDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(UserInventoryConverter::doToEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<UserInventoryBO> findByBelongToIdAndType(Long belongToId, InventoryType inventoryType) {
        RewardUserInventoryDOExample example = new RewardUserInventoryDOExample();
        example.createCriteria()
                .andBelongToIdEqualTo(belongToId)
                .andInventoryTypeEqualTo(inventoryType.getCode())
                .andIsDeletedEqualTo((byte) 0);
        List<RewardUserInventoryDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(UserInventoryConverter::doToEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<UserInventoryBO> findByPublishById(Long publishById) {
        RewardUserInventoryDOExample example = new RewardUserInventoryDOExample();
        example.createCriteria().andPublishByIdEqualTo(publishById).andIsDeletedEqualTo((byte) 0);
        List<RewardUserInventoryDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(UserInventoryConverter::doToEntity)
                .collect(Collectors.toList());
    }
}
