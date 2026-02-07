package com.star.reward.interfaces.rest.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 任务操作请求（暂停/恢复/完成/取消）
 * clientTime 为前端时间戳（秒级），用于减少网络延迟对操作时间的影响
 */
@Data
public class TaskOperationRequest {

    /**
     * 前端操作时间（秒级时间戳），必传
     */
    @NotNull(message = "clientTime 不能为空")
    private Long clientTime;
}
