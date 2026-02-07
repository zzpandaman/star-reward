package com.star.reward.interfaces.rest.dto.request;

import lombok.Data;

/**
 * 商品列表查询请求
 */
@Data
public class ProductQueryRequest {

    /**
     * 页码，从 1 开始，默认 1
     */
    private Integer page = 1;

    /**
     * 每页条数，默认 10
     */
    private Integer pageSize = 10;
}
