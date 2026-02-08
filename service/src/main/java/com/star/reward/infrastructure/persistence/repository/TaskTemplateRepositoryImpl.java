package com.star.reward.infrastructure.persistence.repository;

import com.star.reward.domain.tasktemplate.model.entity.TaskTemplateBO;
import com.star.reward.domain.tasktemplate.model.query.TaskTemplateQueryParam;
import com.star.reward.domain.tasktemplate.repository.TaskTemplateRepository;
import com.star.reward.infrastructure.persistence.converter.TaskTemplateConverter;
import com.star.reward.infrastructure.persistence.dao.entity.RewardTaskTemplateDO;
import com.star.reward.infrastructure.persistence.dao.entity.RewardTaskTemplateDOExample;
import com.star.reward.infrastructure.persistence.dao.mapper.RewardTaskTemplateDOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

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
        List<TaskTemplateBO> list = list(TaskTemplateQueryParam.builder().templateNo(templateNo).build());
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
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
        return list(TaskTemplateQueryParam.builder().build());
    }

    @Override
    public List<TaskTemplateBO> findByIsPreset(Boolean isPreset) {
        return list(TaskTemplateQueryParam.builder().isPreset(isPreset).build());
    }

    @Override
    public List<TaskTemplateBO> listByQuery(TaskTemplateQueryParam param) {
        return list(param);
    }

    @Override
    public long countByQuery(TaskTemplateQueryParam param) {
        return mapper.countByExample(buildExampleForCount(param));
    }

    private List<TaskTemplateBO> list(TaskTemplateQueryParam param) {
        RewardTaskTemplateDOExample example = buildExample(param);
        return mapper.selectByExample(example).stream()
                .map(TaskTemplateConverter::doToEntity)
                .collect(Collectors.toList());
    }

    private static RewardTaskTemplateDOExample buildExample(TaskTemplateQueryParam param) {
        RewardTaskTemplateDOExample example = new RewardTaskTemplateDOExample();
        RewardTaskTemplateDOExample.Criteria c = example.createCriteria();
        if (param != null) {
            if (StringUtils.hasText(param.getTemplateNo())) {
                c.andTemplateNoEqualTo(param.getTemplateNo());
            }
            if (param.getIsPreset() != null) {
                c.andIsPresetEqualTo(param.getIsPreset() ? (byte) 1 : (byte) 0);
            }
            c.andIsDeletedEqualTo(param.getIsDeleted() != null ? param.getIsDeleted() : (byte) 0);
        } else {
            c.andIsDeletedEqualTo((byte) 0);
        }
        if (param != null && StringUtils.hasText(param.getPublishBy())){
            c.andPublishByEqualTo(param.getPublishBy());
        }
        if (param != null && StringUtils.hasText(param.getOrderBy())) {
            example.setOrderByClause(param.getOrderBy());
        }
        if (param != null && param.hasPagination()) {
            example.page(param.getPage(), param.getPageSize());
        }
        return example;
    }

    private static RewardTaskTemplateDOExample buildExampleForCount(TaskTemplateQueryParam param) {
        RewardTaskTemplateDOExample example = new RewardTaskTemplateDOExample();
        RewardTaskTemplateDOExample.Criteria c = example.createCriteria();
        if (param != null) {
            if (StringUtils.hasText(param.getTemplateNo())) {
                c.andTemplateNoEqualTo(param.getTemplateNo());
            }
            if (param.getIsPreset() != null) {
                c.andIsPresetEqualTo(param.getIsPreset() ? (byte) 1 : (byte) 0);
            }
            c.andIsDeletedEqualTo(param.getIsDeleted() != null ? param.getIsDeleted() : (byte) 0);
        } else {
            c.andIsDeletedEqualTo((byte) 0);
        }
        return example;
    }
}
