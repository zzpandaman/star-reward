package com.star.reward.infrastructure.persistence.repository;

import com.star.reward.domain.pointrecord.model.entity.PointRecordBO;
import com.star.reward.domain.pointrecord.model.valueobject.PointRecordType;
import com.star.reward.domain.pointrecord.repository.PointRecordRepository;
import com.star.reward.infrastructure.persistence.converter.PointRecordConverter;
import com.star.reward.infrastructure.persistence.dao.entity.RewardPointRecordDO;
import com.star.reward.infrastructure.persistence.dao.entity.RewardPointRecordDOExample;
import com.star.reward.infrastructure.persistence.dao.mapper.RewardPointRecordDOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
        RewardPointRecordDOExample example = new RewardPointRecordDOExample();
        example.createCriteria().andBelongToIdEqualTo(belongToId);
        example.setOrderByClause("create_time DESC");
        List<RewardPointRecordDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(PointRecordConverter::doToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<PointRecordBO> findByBelongToIdAndType(Long belongToId, PointRecordType pointType) {
        RewardPointRecordDOExample example = new RewardPointRecordDOExample();
        example.createCriteria()
                .andBelongToIdEqualTo(belongToId)
                .andPointTypeEqualTo(pointType.getCode());
        example.setOrderByClause("create_time DESC");
        List<RewardPointRecordDO> list = mapper.selectByExample(example);
        return list.stream()
                .map(PointRecordConverter::doToEntity)
                .collect(Collectors.toList());
    }
}
