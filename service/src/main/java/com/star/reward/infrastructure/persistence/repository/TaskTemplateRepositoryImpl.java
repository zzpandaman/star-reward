package com.star.reward.infrastructure.persistence.repository;

import com.star.reward.domain.tasktemplate.model.entity.TaskTemplateBO;
import com.star.reward.domain.tasktemplate.repository.TaskTemplateRepository;
import com.star.reward.infrastructure.persistence.converter.TaskTemplateConverter;
import com.star.reward.infrastructure.persistence.dao.entity.RewardTaskTemplateDO;
import com.star.reward.infrastructure.persistence.dao.mapper.RewardTaskTemplateMapper;
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
    
    private final RewardTaskTemplateMapper mapper;
    private final TaskTemplateConverter converter;
    
    @Override
    public Optional<TaskTemplateBO> findByTemplateNo(String templateNo) {
        RewardTaskTemplateDO example = new RewardTaskTemplateDO();
        example.setTemplateNo(templateNo);
        example.setIsDeleted((byte) 0);
        
        List<RewardTaskTemplateDO> list = mapper.selectByExample(example);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(converter.toDomain(list.get(0)));
    }
    
    @Override
    public Optional<TaskTemplateBO> findById(Long id) {
        RewardTaskTemplateDO doEntity = mapper.selectByPrimaryKey(id);
        if (doEntity == null || (doEntity.getIsDeleted() != null && doEntity.getIsDeleted() == 1)) {
            return Optional.empty();
        }
        return Optional.of(converter.toDomain(doEntity));
    }
    
    @Override
    public TaskTemplateBO save(TaskTemplateBO taskTemplate) {
        RewardTaskTemplateDO doEntity = converter.TaskTemplateBO2DO(taskTemplate);
        mapper.insertSelective(doEntity);
        return converter.toDomain(doEntity);
    }
    
    @Override
    public TaskTemplateBO update(TaskTemplateBO taskTemplate) {
        RewardTaskTemplateDO doEntity = converter.TaskTemplateBO2DO(taskTemplate);
        mapper.updateByPrimaryKeySelective(doEntity);
        return converter.toDomain(mapper.selectByPrimaryKey(taskTemplate.getId()));
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
        RewardTaskTemplateDO example = new RewardTaskTemplateDO();
        example.setIsDeleted((byte) 0);
        List<RewardTaskTemplateDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(converter::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TaskTemplateBO> findByIsPreset(Boolean isPreset) {
        RewardTaskTemplateDO example = new RewardTaskTemplateDO();
        example.setIsPreset(isPreset != null && isPreset ? (byte) 1 : (byte) 0);
        example.setIsDeleted((byte) 0);
        List<RewardTaskTemplateDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(converter::toDomain)
                .collect(Collectors.toList());
    }
}
