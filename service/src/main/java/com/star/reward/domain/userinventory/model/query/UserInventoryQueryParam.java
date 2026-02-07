package com.star.reward.domain.userinventory.model.query;

import com.star.common.page.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 用户库存查询参数（Domain 层，供 Repository 使用）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserInventoryQueryParam extends PageRequest {

    private String inventoryNo;
    private Long belongToId;
    private String inventoryType;
    private Long publishById;
    private Byte isDeleted;
    private String orderBy;
}
