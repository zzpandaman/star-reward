package com.star.reward.interfaces.rest.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 主键 ID 请求（用于 delete 等操作）
 */
@Data
public class IdRequest {

    @NotNull(message = "id 不能为空")
    private Long id;
}
