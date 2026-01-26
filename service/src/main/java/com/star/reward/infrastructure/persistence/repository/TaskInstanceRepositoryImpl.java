package com.star.reward.infrastructure.persistence.repository;

import com.star.reward.domain.taskinstance.model.entity.TaskInstanceBO;
import com.star.reward.domain.taskinstance.model.valueobject.InstanceState;
import com.star.reward.domain.taskinstance.repository.TaskInstanceRepository;
import com.star.reward.infrastructure.persistence.converter.TaskInstanceConverter;
import com.star.reward.infrastructure.persistence.dao.entity.RewardTaskInstanceDO;
import com.star.reward.infrastructure.persistence.dao.mapper.RewardTaskInstanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 任务实例仓储实现
 */
@Repository
@RequiredArgsConstructor
public class TaskInstanceRepositoryImpl implements TaskInstanceRepository {
    
    private final RewardTaskInstanceMapper mapper;
    private final TaskInstanceConverter converter;
    
    @Override
    public Optional<TaskInstanceBO> findByInstanceNo(String instanceNo) {
        RewardTaskInstanceDO example = new RewardTaskInstanceDO();
        example.setInstanceNo(instanceNo);
        example.setIsDeleted((byte) 0);
        
        List<RewardTaskInstanceDO> list = mapper.selectByExample(example);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(converter.toDomain(list.get(0)));
    }
    
    @Override
    public Optional<TaskInstanceBO> findById(Long id) {
        RewardTaskInstanceDO doEntity = mapper.selectByPrimaryKey(id);
        if (doEntity == null || (doEntity.getIsDeleted() != null && doEntity.getIsDeleted() == 1)) {
            return Optional.empty();
        }
        return Optional.of(converter.toDomain(doEntity));
    }
    
    @Override
    public TaskInstanceBO save(TaskInstanceBO taskInstance) {
        RewardTaskInstanceDO doEntity = converter.toDO(taskInstance);
        mapper.insertSelective(doEntity);
        return converter.toDomain(doEntity);
    }
    
    @Override
    public TaskInstanceBO update(TaskInstanceBO taskInstance) {
        RewardTaskInstanceDO doEntity = converter.toDO(taskInstance);
        mapper.updateByPrimaryKeySelective(doEntity);
        return converter.toDomain(mapper.selectByPrimaryKey(taskInstance.getId()));
    }
    
    @Override
    public void delete(Long id) {
        RewardTaskInstanceDO doEntity = mapper.selectByPrimaryKey(id);
        if (doEntity != null) {
            doEntity.setIsDeleted((byte) 1);
            mapper.updateByPrimaryKeySelective(doEntity);
        }
    }
    
    @Override
    public List<TaskInstanceBO> findByTemplateNo(String templateNo) {
        RewardTaskInstanceDO example = new RewardTaskInstanceDO();
        example.setTemplateNo(templateNo);
        example.setIsDeleted((byte) 0);
        List<RewardTaskInstanceDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(converter::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskInstanceBO> findByExecuteById(Long executeById) {
        RewardTaskInstanceDO example = new RewardTaskInstanceDO();
        example.setExecuteById(executeById);
        example.setIsDeleted((byte) 0);
        List<RewardTaskInstanceDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(converter::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskInstanceBO> findByInstanceState(InstanceState instanceState) {
        RewardTaskInstanceDO example = new RewardTaskInstanceDO();
        example.setInstanceState(instanceState.getCode());
        example.setIsDeleted((byte) 0);
        List<RewardTaskInstanceDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(converter::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskInstanceBO> findByExecuteByIdAndState(Long executeById, InstanceState instanceState) {
        RewardTaskInstanceDO example = new RewardTaskInstanceDO();
        example.setExecuteById(executeById);
        example.setInstanceState(instanceState.getCode());
        example.setIsDeleted((byte) 0);
        List<RewardTaskInstanceDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(converter::toDomain)
                .collect(Collectors.toList());
    }
}
