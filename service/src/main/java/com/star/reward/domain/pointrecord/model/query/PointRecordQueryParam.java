package com.star.reward.domain.pointrecord.model.query;

import com.star.common.page.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 积分记录查询参数（Domain 层，供 Repository 使用）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PointRecordQueryParam extends PageRequest {

    /**
     * 所属人 ID
     */
    private Long belongToId;

    /**
     * 类型：earn / spend
     */
    private String type;
}
