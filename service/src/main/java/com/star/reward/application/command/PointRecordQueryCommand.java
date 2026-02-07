package com.star.reward.application.command;

import lombok.Data;

/**
 * 积分记录查询用例入参
 */
@Data
public class PointRecordQueryCommand {

    /**
     * 所属人 ID，通常由 ApplicationService 从 CurrentUserContext 注入
     */
    private Long belongToId;

    /**
     * 类型：earn-获取, spend-消耗，可选
     */
    private String type;

    /**
     * 页码（从 1 开始），可选，用于分页
     */
    private Integer page;

    /**
     * 每页条数，可选，用于分页
     */
    private Integer pageSize;
}
