package com.star.reward.domain.product.model.query;

import com.star.common.page.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 商品查询参数（Domain 层，供 Repository 使用）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductQueryParam extends PageRequest {

    private String productNo;
    private Boolean isPreset;
    private Long publishById;
    private Byte isDeleted;
    private String orderBy;
}
