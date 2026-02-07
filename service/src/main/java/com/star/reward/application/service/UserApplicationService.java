package com.star.reward.application.service;

import com.star.common.page.PageResponse;
import com.star.reward.domain.userinventory.model.entity.UserInventoryBO;
import com.star.reward.domain.userinventory.model.valueobject.InventoryType;
import com.star.reward.domain.userinventory.repository.UserInventoryRepository;
import com.star.reward.interfaces.rest.dto.response.InventoryResponse;
import com.star.reward.interfaces.rest.dto.response.UserPointsResponse;
import com.star.reward.shared.context.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户应用服务
 */
@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserInventoryRepository userInventoryRepository;

    /**
     * 获取用户积分
     */
    public UserPointsResponse getUserPoints() {
        CurrentUserContext user = CurrentUserContext.get();

        List<UserInventoryBO> pointInventories = userInventoryRepository
                .findByBelongToIdAndType(user.getUserId(), InventoryType.POINT);

        BigDecimal points = BigDecimal.ZERO;
        if (!pointInventories.isEmpty()) {
            points = pointInventories.get(0).getQuantity();
        }

        return UserPointsResponse.builder()
                .points(points)
                .build();
    }

    /**
     * 获取用户背包
     */
    public PageResponse<InventoryResponse> getUserInventory() {
        CurrentUserContext user = CurrentUserContext.get();

        List<UserInventoryBO> inventories = userInventoryRepository
                .findByBelongToIdAndType(user.getUserId(), InventoryType.PRODUCT);

        List<InventoryResponse> responses = inventories.stream()
                .map(this::toInventoryResponse)
                .collect(Collectors.toList());

        return PageResponse.of(responses, responses.size(), 1, responses.size());
    }

    private InventoryResponse toInventoryResponse(UserInventoryBO bo) {
        return InventoryResponse.builder()
                .id(bo.getId())
                .inventoryNo(bo.getInventoryNo())
                .name(bo.getName())
                .description(bo.getDescription())
                .quantity(bo.getQuantity())
                .unit(bo.getUnit())
                .createTime(bo.getCreateTime())
                .updateTime(bo.getUpdateTime())
                .build();
    }
}
