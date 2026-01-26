package com.star.reward.api.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 奖励查询请求 DTO
 */
@Data
public class RewardQueryRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "奖励ID不能为空")
    private Long id;

    private String name;
}
