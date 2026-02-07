package com.star.reward.domain.pointrecord.model.entity;

import com.star.reward.domain.pointrecord.model.constant.PointRecordConstants;
import com.star.reward.domain.pointrecord.model.valueobject.PointRecordType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 积分记录领域实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointRecordBO {

    private Long id;
    private String recordNo;
    private PointRecordType pointType;
    private BigDecimal amount;
    private String belongTo;
    private Long belongToId;
    private String relatedId;
    private String description;
    private String createBy;
    private Long createById;
    private LocalDateTime createTime;

    /**
     * 工厂方法：创建获取类积分记录
     */
    public static PointRecordBO createEarn(String recordNo, BigDecimal amount, String belongTo,
            Long belongToId, String relatedId, String description, LocalDateTime createTime) {
        return PointRecordBO.builder()
                .recordNo(recordNo)
                .pointType(PointRecordType.EARN)
                .amount(amount)
                .belongTo(belongTo)
                .belongToId(belongToId)
                .relatedId(relatedId)
                .description(description)
                .createBy(belongTo)
                .createById(belongToId)
                .createTime(createTime)
                .build();
    }

    /**
     * 工厂方法：创建消耗类积分记录
     */
    public static PointRecordBO createSpend(String recordNo, BigDecimal amount, String belongTo,
            Long belongToId, String relatedId, String description, LocalDateTime createTime) {
        return PointRecordBO.builder()
                .recordNo(recordNo)
                .pointType(PointRecordType.SPEND)
                .amount(amount)
                .belongTo(belongTo)
                .belongToId(belongToId)
                .relatedId(relatedId)
                .description(description)
                .createBy(belongTo)
                .createById(belongToId)
                .createTime(createTime)
                .build();
    }
}
