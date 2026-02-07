package com.star.reward.infrastructure.persistence.repository;

import com.star.reward.domain.tasktemplate.model.entity.TaskTemplateBO;
import com.star.reward.domain.tasktemplate.repository.TaskTemplateRepository;
import com.star.reward.infrastructure.persistence.converter.TaskTemplateConverter;
import com.star.reward.infrastructure.persistence.dao.entity.RewardTaskTemplateDO;
import com.star.reward.infrastructure.persistence.dao.mapper.RewardTaskTemplateDOMapper;
import com.star.reward.infrastructure.persistence.dao.entity.RewardTaskTemplateDOExample;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 任务模板仓储实现
 */
@Repository
@RequiredArgsConstructor
public class TaskTemplateRepositoryImpl implements TaskTemplateRepository {

    private final RewardTaskTemplateDOMapper mapper;
    
    @Override
    public Optional<TaskTemplateBO> findByTemplateNo(String templateNo) {
        RewardTaskTemplateDOExample example = new RewardTaskTemplateDOExample();
        example.createCriteria().andTemplateNoEqualTo(templateNo).andIsDeletedEqualTo((byte) 0);
        
        List<RewardTaskTemplateDO> list = mapper.selectByExample(example);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(TaskTemplateConverter.doToEntity(list.get(0)));
    }
    
    @Override
    public Optional<TaskTemplateBO> findById(Long id) {
        RewardTaskTemplateDO doEntity = mapper.selectByPrimaryKey(id);
        if (doEntity == null || (doEntity.getIsDeleted() != null && doEntity.getIsDeleted() == 1)) {
            return Optional.empty();
        }
        return Optional.of(TaskTemplateConverter.doToEntity(doEntity));
    }
    
    @Override
    public TaskTemplateBO save(TaskTemplateBO taskTemplate) {
        RewardTaskTemplateDO doEntity = TaskTemplateConverter.entityToDo(taskTemplate);
        mapper.insertSelective(doEntity);
        return TaskTemplateConverter.doToEntity(doEntity);
    }
    
    @Override
    public TaskTemplateBO update(TaskTemplateBO taskTemplate) {
        RewardTaskTemplateDO doEntity = TaskTemplateConverter.partialEntityToDo(taskTemplate);
        mapper.updateByPrimaryKeySelective(doEntity);
        return TaskTemplateConverter.doToEntity(mapper.selectByPrimaryKey(taskTemplate.getId()));
    }
    
    @Override
    public void delete(Long id) {
        RewardTaskTemplateDO doEntity = mapper.selectByPrimaryKey(id);
        if (doEntity != null) {
            doEntity.setIsDeleted((byte) 1);
            mapper.updateByPrimaryKeySelective(doEntity);
        }
    }
    
    @Override
    public List<TaskTemplateBO> findAll() {
        RewardTaskTemplateDOExample example = new RewardTaskTemplateDOExample();
        example.createCriteria().andIsDeletedEqualTo((byte) 0);
        List<RewardTaskTemplateDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(TaskTemplateConverter::doToEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskTemplateBO> findByIsPreset(Boolean isPreset) {
        RewardTaskTemplateDOExample example = new RewardTaskTemplateDOExample();
        example.createCriteria()
                .andIsPresetEqualTo(isPreset != null && isPreset ? (byte) 1 : (byte) 0)
                .andIsDeletedEqualTo((byte) 0);
        List<RewardTaskTemplateDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(TaskTemplateConverter::doToEntity)
                .collect(Collectors.toList());
    }
}
