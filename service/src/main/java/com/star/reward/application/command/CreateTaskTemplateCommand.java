package com.star.reward.application.command;

import lombok.Data;

/**
 * 创建任务模板用例入参
 */
@Data
public class CreateTaskTemplateCommand {

    private String name;
    private String description;
}
