package com.star.reward.infrastructure.persistence.repository;

import com.star.reward.domain.taskinstance.model.entity.TaskInstanceBO;
import com.star.reward.domain.taskinstance.model.valueobject.InstanceState;
import com.star.reward.domain.taskinstance.repository.TaskInstanceRepository;
import com.star.reward.infrastructure.persistence.converter.TaskInstanceConverter;
import com.star.reward.infrastructure.persistence.dao.entity.RewardTaskInstanceDO;
import com.star.reward.infrastructure.persistence.dao.entity.RewardTaskInstanceDOExample;
import com.star.reward.infrastructure.persistence.dao.mapper.RewardTaskInstanceDOMapper;
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

    private final RewardTaskInstanceDOMapper mapper;
    
    @Override
    public Optional<TaskInstanceBO> findByInstanceNo(String instanceNo) {
        RewardTaskInstanceDOExample example = new RewardTaskInstanceDOExample();
        example.createCriteria().andInstanceNoEqualTo(instanceNo).andIsDeletedEqualTo((byte) 0);
        
        List<RewardTaskInstanceDO> list = mapper.selectByExample(example);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(TaskInstanceConverter.doToEntity(list.get(0)));
    }
    
    @Override
    public Optional<TaskInstanceBO> findById(Long id) {
        RewardTaskInstanceDO doEntity = mapper.selectByPrimaryKey(id);
        if (doEntity == null || (doEntity.getIsDeleted() != null && doEntity.getIsDeleted() == 1)) {
            return Optional.empty();
        }
        return Optional.of(TaskInstanceConverter.doToEntity(doEntity));
    }
    
    @Override
    public TaskInstanceBO save(TaskInstanceBO taskInstance) {
        RewardTaskInstanceDO doEntity = TaskInstanceConverter.entityToDo(taskInstance);
        mapper.insertSelective(doEntity);
        return TaskInstanceConverter.doToEntity(doEntity);
    }
    
    @Override
    public TaskInstanceBO update(TaskInstanceBO taskInstance) {
        RewardTaskInstanceDO doEntity = TaskInstanceConverter.entityToDo(taskInstance);
        mapper.updateByPrimaryKeySelective(doEntity);
        return TaskInstanceConverter.doToEntity(mapper.selectByPrimaryKey(taskInstance.getId()));
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
        RewardTaskInstanceDOExample example = new RewardTaskInstanceDOExample();
        example.createCriteria().andTemplateNoEqualTo(templateNo).andIsDeletedEqualTo((byte) 0);
        List<RewardTaskInstanceDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(TaskInstanceConverter::doToEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskInstanceBO> findByExecuteById(Long executeById) {
        RewardTaskInstanceDOExample example = new RewardTaskInstanceDOExample();
        example.createCriteria().andExecuteByIdEqualTo(executeById).andIsDeletedEqualTo((byte) 0);
        List<RewardTaskInstanceDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(TaskInstanceConverter::doToEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskInstanceBO> findByInstanceState(InstanceState instanceState) {
        RewardTaskInstanceDOExample example = new RewardTaskInstanceDOExample();
        example.createCriteria().andInstanceStateEqualTo(instanceState.getCode()).andIsDeletedEqualTo((byte) 0);
        List<RewardTaskInstanceDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(TaskInstanceConverter::doToEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskInstanceBO> findByExecuteByIdAndState(Long executeById, InstanceState instanceState) {
        RewardTaskInstanceDOExample example = new RewardTaskInstanceDOExample();
        example.createCriteria()
                .andExecuteByIdEqualTo(executeById)
                .andInstanceStateEqualTo(instanceState.getCode())
                .andIsDeletedEqualTo((byte) 0);
        List<RewardTaskInstanceDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(TaskInstanceConverter::doToEntity)
                .collect(Collectors.toList());
    }
}
