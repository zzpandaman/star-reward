package com.star.reward.infrastructure.persistence.repository;

import com.star.reward.domain.taskinstance.model.entity.TaskInstanceBO;
import com.star.reward.domain.taskinstance.model.query.TaskInstanceQueryParam;
import com.star.reward.domain.taskinstance.model.valueobject.InstanceState;
import com.star.reward.domain.taskinstance.repository.TaskInstanceRepository;
import com.star.reward.infrastructure.persistence.converter.TaskInstanceConverter;
import com.star.reward.infrastructure.persistence.dao.entity.RewardTaskInstanceDO;
import com.star.reward.infrastructure.persistence.dao.entity.RewardTaskInstanceDOExample;
import com.star.reward.infrastructure.persistence.dao.mapper.RewardTaskInstanceDOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
        List<TaskInstanceBO> list = list(TaskInstanceQueryParam.builder().instanceNo(instanceNo).build());
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
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
        return list(TaskInstanceQueryParam.builder().templateNo(templateNo).build());
    }

    @Override
    public List<TaskInstanceBO> findByExecuteById(Long executeById) {
        return list(TaskInstanceQueryParam.builder().executeById(executeById).build());
    }

    @Override
    public List<TaskInstanceBO> findByInstanceState(InstanceState instanceState) {
        return list(TaskInstanceQueryParam.builder()
                .instanceState(instanceState != null ? instanceState.getCode() : null)
                .build());
    }

    @Override
    public List<TaskInstanceBO> findByExecuteByIdAndState(Long executeById, InstanceState instanceState) {
        return list(TaskInstanceQueryParam.builder()
                .executeById(executeById)
                .instanceState(instanceState != null ? instanceState.getCode() : null)
                .build());
    }

    @Override
    public List<TaskInstanceBO> listByQuery(TaskInstanceQueryParam param) {
        return list(param);
    }

    @Override
    public long countByQuery(TaskInstanceQueryParam param) {
        return mapper.countByExample(buildExampleForCount(param));
    }

    private List<TaskInstanceBO> list(TaskInstanceQueryParam param) {
        RewardTaskInstanceDOExample example = buildExample(param);
        return mapper.selectByExampleWithBLOBs(example).stream()
                .map(TaskInstanceConverter::doToEntity)
                .collect(Collectors.toList());
    }

    private static RewardTaskInstanceDOExample buildExample(TaskInstanceQueryParam param) {
        RewardTaskInstanceDOExample example = new RewardTaskInstanceDOExample();
        RewardTaskInstanceDOExample.Criteria c = example.createCriteria();
        if (param != null) {
            if (StringUtils.hasText(param.getInstanceNo())) {
                c.andInstanceNoEqualTo(param.getInstanceNo());
            }
            if (StringUtils.hasText(param.getTemplateNo())) {
                c.andTemplateNoEqualTo(param.getTemplateNo());
            }
            if (param.getExecuteById() != null) {
                c.andExecuteByIdEqualTo(param.getExecuteById());
            }
            if (StringUtils.hasText(param.getInstanceState())) {
                c.andInstanceStateEqualTo(param.getInstanceState());
            }
            if (!CollectionUtils.isEmpty(param.getInstanceStates())) {
                c.andInstanceStateIn(param.getInstanceStates());
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
    private static RewardTaskInstanceDOExample buildExampleForCount(TaskInstanceQueryParam param) {
        RewardTaskInstanceDOExample example = new RewardTaskInstanceDOExample();
        RewardTaskInstanceDOExample.Criteria c = example.createCriteria();
        if (param != null) {
            if (StringUtils.hasText(param.getInstanceNo())) {
                c.andInstanceNoEqualTo(param.getInstanceNo());
            }
            if (StringUtils.hasText(param.getTemplateNo())) {
                c.andTemplateNoEqualTo(param.getTemplateNo());
            }
            if (param.getExecuteById() != null) {
                c.andExecuteByIdEqualTo(param.getExecuteById());
            }
            if (StringUtils.hasText(param.getInstanceState())) {
                c.andInstanceStateEqualTo(param.getInstanceState());
            }
            if (!CollectionUtils.isEmpty(param.getInstanceStates())) {
                c.andInstanceStateIn(param.getInstanceStates());
            }
            c.andIsDeletedEqualTo(param.getIsDeleted() != null ? param.getIsDeleted() : (byte) 0);
        } else {
            c.andIsDeletedEqualTo((byte) 0);
        }
        return example;
    }
}
