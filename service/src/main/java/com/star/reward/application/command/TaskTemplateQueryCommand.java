package com.star.reward.application.command;

import com.star.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务模板列表查询用例入参
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskTemplateQueryCommand extends PageRequest {

    private String templateNo;
    private Boolean isPreset;
    private Byte isDeleted;
    private String orderBy;
}
