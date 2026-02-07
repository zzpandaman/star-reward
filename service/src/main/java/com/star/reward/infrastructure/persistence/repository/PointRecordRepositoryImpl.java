package com.star.reward.infrastructure.persistence.repository;

import com.star.reward.domain.pointrecord.model.entity.PointRecordBO;
import com.star.reward.domain.pointrecord.model.query.PointRecordQueryParam;
import com.star.reward.domain.pointrecord.model.valueobject.PointRecordType;
import com.star.reward.domain.pointrecord.repository.PointRecordRepository;
import com.star.reward.infrastructure.persistence.converter.PointRecordConverter;
import com.star.reward.infrastructure.persistence.dao.entity.RewardPointRecordDO;
import com.star.reward.infrastructure.persistence.dao.entity.RewardPointRecordDOExample;
import com.star.reward.infrastructure.persistence.dao.mapper.RewardPointRecordDOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 积分记录仓储实现
 */
@Repository
@RequiredArgsConstructor
public class PointRecordRepositoryImpl implements PointRecordRepository {

    private final RewardPointRecordDOMapper mapper;

    @Override
    public Optional<PointRecordBO> findById(Long id) {
        RewardPointRecordDO doEntity = mapper.selectByPrimaryKey(id);
        if (doEntity == null) {
            return Optional.empty();
        }
        return Optional.of(PointRecordConverter.doToEntity(doEntity));
    }

    @Override
    public PointRecordBO save(PointRecordBO pointRecord) {
        RewardPointRecordDO doEntity = PointRecordConverter.entityToDo(pointRecord);
        mapper.insertSelective(doEntity);
        return PointRecordConverter.doToEntity(doEntity);
    }

    @Override
    public List<PointRecordBO> findByBelongToId(Long belongToId) {
        return list(PointRecordQueryParam.builder().belongToId(belongToId).build());
    }

    @Override
    public List<PointRecordBO> findByBelongToIdAndType(Long belongToId, PointRecordType pointType) {
        return list(PointRecordQueryParam.builder()
                .belongToId(belongToId)
                .type(pointType != null ? pointType.getCode() : null)
                .build());
    }

    @Override
    public List<PointRecordBO> listByQuery(PointRecordQueryParam param) {
        return list(param);
    }

    private List<PointRecordBO> list(PointRecordQueryParam param) {
        RewardPointRecordDOExample example = buildExample(param);
        return mapper.selectByExample(example).stream()
                .map(PointRecordConverter::doToEntity)
                .collect(Collectors.toList());
    }

    /**
     * buildExample 范式：从 Param 构建 Example，仅使用 createCriteria().andXxx()，支持 null 可选条件
     */
    private static RewardPointRecordDOExample buildExample(PointRecordQueryParam param) {
        RewardPointRecordDOExample example = new RewardPointRecordDOExample();
        RewardPointRecordDOExample.Criteria c = example.createCriteria();
        if (param != null && param.getBelongToId() != null) {
            c.andBelongToIdEqualTo(param.getBelongToId());
        }
        if (param != null && StringUtils.hasText(param.getType())) {
            c.andPointTypeEqualTo(param.getType());
        }
        example.setOrderByClause("create_time DESC");
        if (param != null && param.hasPagination()) {
            example.page(param.getPage(), param.getPageSize());
        }
        return example;
    }
}
