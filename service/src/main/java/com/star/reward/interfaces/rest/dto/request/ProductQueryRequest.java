package com.star.reward.interfaces.rest.dto.request;

import com.star.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品列表查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductQueryRequest extends PageRequest {
}
