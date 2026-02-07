package com.star.reward.interfaces.rest.dto.request;

import com.star.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务模板列表查询请求（支持 POST JSON 分页与条件参数）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskTemplateQueryRequest extends PageRequest {

    /**
     * 模板编号（精确匹配）
     */
    private String templateNo;

    /**
     * 是否预设：true/false
     */
    private Boolean isPreset;

    /**
     * 是否删除：0-未删除，1-已删除；默认 0
     */
    private Byte isDeleted = 0;

    /**
     * 排序：如 id DESC、create_time ASC
     */
    private String orderBy;
}
