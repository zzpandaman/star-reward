package com.star.reward.application.command;

import com.star.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 积分记录查询用例入参
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PointRecordQueryCommand extends PageRequest {

    /**
     * 所属人 ID，通常由 ApplicationService 从 CurrentUserContext 注入
     */
    private Long belongToId;

    /**
     * 类型：earn-获取, spend-消耗，可选
     */
    private String type;
}
