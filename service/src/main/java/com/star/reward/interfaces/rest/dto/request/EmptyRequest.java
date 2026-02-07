package com.star.reward.interfaces.rest.dto.request;

import lombok.Data;

/**
 * 空请求体（用于无参数的 POST 接口，保证 body 必传、不传时返回 400）
 */
@Data
public class EmptyRequest {
}
