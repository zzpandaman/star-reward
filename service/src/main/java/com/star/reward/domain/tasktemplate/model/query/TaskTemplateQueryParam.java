package com.star.reward.domain.tasktemplate.model.query;

import com.star.common.page.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 任务模板查询参数（Domain 层，供 Repository 使用）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TaskTemplateQueryParam extends PageRequest {

    private String templateNo;
    private Boolean isPreset;
    private Byte isDeleted;
    private String orderBy;
}
