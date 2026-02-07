package com.star.reward.application.command;

import lombok.Data;

/**
 * 开始任务用例入参
 */
@Data
public class StartTaskCommand {

    private Long taskTemplateId;
}
