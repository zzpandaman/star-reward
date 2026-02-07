package com.star.reward.domain.taskinstance.model.query;

import com.star.common.page.PageRequest;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 任务实例查询参数（Domain 层，供 Repository 使用）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TaskInstanceQueryParam extends PageRequest {

    private String instanceNo;
    private String templateNo;
    private Long executeById;
    private String instanceState;
    /** 多状态 IN 查询，如 进行中=RUNNING+PAUSED */
    private List<String> instanceStates;
    private Byte isDeleted;
    private String orderBy;
}
