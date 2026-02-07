package com.star.reward.application.command;

import lombok.Data;

/**
 * 积分记录查询用例入参
 */
@Data
public class PointRecordQueryCommand {

    /**
     * 类型：earn-获取, spend-消耗，可选
     */
    private String type;
}
