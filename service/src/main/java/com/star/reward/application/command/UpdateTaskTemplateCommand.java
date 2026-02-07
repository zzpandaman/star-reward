package com.star.reward.application.command;

import lombok.Data;

/**
 * 更新任务模板用例入参
 */
@Data
public class UpdateTaskTemplateCommand {

    private String name;
    private String description;
}
