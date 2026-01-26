package com.star.reward.api.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 奖励响应 DTO
 */
@Data
public class RewardResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private Integer amount;
    private LocalDateTime createTime;
}
