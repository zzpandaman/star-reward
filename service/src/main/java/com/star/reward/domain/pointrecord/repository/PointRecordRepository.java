package com.star.reward.domain.pointrecord.repository;

import com.star.reward.domain.pointrecord.model.entity.PointRecordBO;
import com.star.reward.domain.pointrecord.model.query.PointRecordQueryParam;
import com.star.reward.domain.pointrecord.model.valueobject.PointRecordType;

import java.util.List;
import java.util.Optional;

/**
 * 积分记录仓储接口
 */
public interface PointRecordRepository {

    /**
     * 根据ID查询
     */
    Optional<PointRecordBO> findById(Long id);

    /**
     * 保存积分记录
     */
    PointRecordBO save(PointRecordBO pointRecord);

    /**
     * 根据所属人ID查询
     */
    List<PointRecordBO> findByBelongToId(Long belongToId);

    /**
     * 根据所属人ID和类型查询
     */
    List<PointRecordBO> findByBelongToIdAndType(Long belongToId, PointRecordType pointType);

    /**
     * 根据查询条件分页/列表查询，使用 buildExample 范式
     */
    List<PointRecordBO> listByQuery(PointRecordQueryParam param);
}
