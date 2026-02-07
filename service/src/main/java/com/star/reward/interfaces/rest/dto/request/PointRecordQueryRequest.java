package com.star.reward.interfaces.rest.dto.request;

import lombok.Data;

/**
 * 积分记录查询请求
 */
@Data
public class PointRecordQueryRequest {

    /**
     * 类型：earn-获取, spend-消耗，可选
     */
    private String type;
}
