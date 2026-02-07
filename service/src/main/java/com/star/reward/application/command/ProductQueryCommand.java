package com.star.reward.application.command;

import com.star.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品列表查询用例入参
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductQueryCommand extends PageRequest {
}
