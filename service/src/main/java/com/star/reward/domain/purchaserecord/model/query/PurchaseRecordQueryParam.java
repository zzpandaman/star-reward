package com.star.reward.domain.purchaserecord.model.query;

import com.star.common.page.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 购买记录查询参数（Domain 层，供 Repository 使用）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PurchaseRecordQueryParam extends PageRequest {

    private String purchaseNo;
    private String productNo;
    private Long purchaseById;
    private Long publishById;
    private Byte isDeleted;
    private String orderBy;
}
