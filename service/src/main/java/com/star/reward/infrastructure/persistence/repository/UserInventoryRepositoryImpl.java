package com.star.reward.infrastructure.persistence.repository;

import com.star.reward.domain.userinventory.model.entity.UserInventoryBO;
import com.star.reward.domain.userinventory.model.valueobject.InventoryType;
import com.star.reward.domain.userinventory.repository.UserInventoryRepository;
import com.star.reward.infrastructure.persistence.converter.UserInventoryConverter;
import com.star.reward.infrastructure.persistence.dao.entity.RewardUserInventoryDO;
import com.star.reward.infrastructure.persistence.dao.mapper.RewardUserInventoryMapper;
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
    
    private final RewardUserInventoryMapper mapper;
    private final UserInventoryConverter converter;
    
    @Override
    public Optional<UserInventoryBO> findByInventoryNo(String inventoryNo) {
        RewardUserInventoryDO example = new RewardUserInventoryDO();
        example.setInventoryNo(inventoryNo);
        example.setIsDeleted((byte) 0);
        
        List<RewardUserInventoryDO> list = mapper.selectByExample(example);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(converter.toDomain(list.get(0)));
    }
    
    @Override
    public Optional<UserInventoryBO> findById(Long id) {
        RewardUserInventoryDO doEntity = mapper.selectByPrimaryKey(id);
        if (doEntity == null || (doEntity.getIsDeleted() != null && doEntity.getIsDeleted() == 1)) {
            return Optional.empty();
        }
        return Optional.of(converter.toDomain(doEntity));
    }
    
    @Override
    public UserInventoryBO save(UserInventoryBO userInventory) {
        RewardUserInventoryDO doEntity = converter.UserInventoryBO2DO(userInventory);
        mapper.insertSelective(doEntity);
        return converter.toDomain(doEntity);
    }
    
    @Override
    public UserInventoryBO update(UserInventoryBO userInventory) {
        RewardUserInventoryDO doEntity = converter.UserInventoryBO2DO(userInventory);
        mapper.updateByPrimaryKeySelective(doEntity);
        return converter.toDomain(mapper.selectByPrimaryKey(userInventory.getId()));
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
        RewardUserInventoryDO example = new RewardUserInventoryDO();
        example.setBelongToId(belongToId);
        example.setIsDeleted((byte) 0);
        List<RewardUserInventoryDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(converter::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<UserInventoryBO> findByInventoryType(InventoryType inventoryType) {
        RewardUserInventoryDO example = new RewardUserInventoryDO();
        example.setInventoryType(inventoryType.getCode());
        example.setIsDeleted((byte) 0);
        List<RewardUserInventoryDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(converter::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<UserInventoryBO> findByBelongToIdAndType(Long belongToId, InventoryType inventoryType) {
        RewardUserInventoryDO example = new RewardUserInventoryDO();
        example.setBelongToId(belongToId);
        example.setInventoryType(inventoryType.getCode());
        example.setIsDeleted((byte) 0);
        List<RewardUserInventoryDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(converter::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<UserInventoryBO> findByPublishById(Long publishById) {
        RewardUserInventoryDO example = new RewardUserInventoryDO();
        example.setPublishById(publishById);
        example.setIsDeleted((byte) 0);
        List<RewardUserInventoryDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(converter::toDomain)
                .collect(Collectors.toList());
    }
}
