package com.star.reward.infrastructure.persistence.converter;

import com.star.reward.domain.pointrecord.model.entity.PointRecordBO;
import com.star.reward.domain.pointrecord.model.valueobject.PointRecordType;
import com.star.reward.infrastructure.persistence.dao.entity.RewardPointRecordDO;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 积分记录转换器（Infrastructure：DO ↔ Entity）
 */
public final class PointRecordConverter {

    private PointRecordConverter() {
    }

    /**
     * DO → Entity
     */
    public static PointRecordBO doToEntity(RewardPointRecordDO doEntity) {
        if (doEntity == null) {
            return null;
        }
        PointRecordBO bo = new PointRecordBO();
        bo.setId(doEntity.getId());
        bo.setRecordNo(doEntity.getRecordNo());
        bo.setPointType(doEntity.getPointType() != null ? PointRecordType.fromCode(doEntity.getPointType()) : null);
        bo.setAmount(doEntity.getAmount());
        bo.setBelongTo(doEntity.getBelongTo());
        bo.setBelongToId(doEntity.getBelongToId());
        bo.setRelatedId(doEntity.getRelatedId());
        bo.setDescription(doEntity.getDescription());
        bo.setCreateBy(doEntity.getCreateBy());
        bo.setCreateById(doEntity.getCreateById());
        bo.setCreateTime(convertToLocalDateTime(doEntity.getCreateTime()));
        return bo;
    }

    /**
     * Entity → DO
     */
    public static RewardPointRecordDO entityToDo(PointRecordBO source) {
        if (source == null) {
            return null;
        }
        RewardPointRecordDO target = new RewardPointRecordDO();
        target.setId(source.getId());
        target.setRecordNo(source.getRecordNo());
        target.setPointType(source.getPointType() != null ? source.getPointType().getCode() : null);
        target.setAmount(source.getAmount());
        target.setBelongTo(source.getBelongTo());
        target.setBelongToId(source.getBelongToId());
        target.setRelatedId(source.getRelatedId());
        target.setDescription(source.getDescription());
        target.setCreateBy(source.getCreateBy());
        target.setCreateById(source.getCreateById());
        target.setCreateTime(convertToDate(source.getCreateTime()));
        return target;
    }

    private static LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private static Date convertToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
