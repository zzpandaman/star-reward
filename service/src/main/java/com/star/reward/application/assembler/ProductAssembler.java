package com.star.reward.application.assembler;

import com.star.reward.application.command.CreateProductCommand;
import com.star.reward.application.command.ProductQueryCommand;
import com.star.reward.application.command.UpdateProductCommand;
import com.star.reward.domain.product.model.entity.ProductBO;
import com.star.reward.domain.product.model.query.ProductQueryParam;
import com.star.reward.interfaces.rest.dto.response.ProductResponse;

import java.time.LocalDateTime;

/**
 * 商品转换器（Application → Domain 边界）
 * Command → Entity、Entity → Response
 */
public final class ProductAssembler {

    private ProductAssembler() {
    }

    /**
     * Command → QueryParam（供 Repository listByQuery 使用）
     */
    public static ProductQueryParam commandToQueryParam(ProductQueryCommand command) {
        if (command == null) {
            return ProductQueryParam.builder().build();
        }
        ProductQueryParam param = ProductQueryParam.builder().build();
        param.setPage(command.getPage());
        param.setPageSize(command.getPageSize());
        return param;
    }

    /**
     * Command → Entity（仅映射业务字段）
     */
    public static ProductBO createCommandToEntity(CreateProductCommand command) {
        if (command == null) {
            return null;
        }
        ProductBO bo = new ProductBO();
        bo.setName(command.getName());
        bo.setDescription(command.getDescription());
        bo.setPrice(command.getPrice());
        bo.setMinQuantity(command.getMinQuantity());
        bo.setMinUnit(command.getUnit());
        return bo;
    }

    /**
     * UpdateCommand → Partial Entity（仅 id、非空业务字段、审计字段，供 updateByPrimaryKeySelective 用）
     */
    public static ProductBO updateCommandToPartialEntity(Long id, UpdateProductCommand command,
                                                         String updateBy, Long updateById, LocalDateTime updateTime) {
        if (id == null) {
            return null;
        }
        ProductBO bo = new ProductBO();
        bo.setId(id);
        bo.setUpdateBy(updateBy);
        bo.setUpdateById(updateById);
        bo.setUpdateTime(updateTime);
        if (command != null) {
            if (command.getName() != null) {
                bo.setName(command.getName());
            }
            if (command.getDescription() != null) {
                bo.setDescription(command.getDescription());
            }
            if (command.getPrice() != null) {
                bo.setPrice(command.getPrice());
            }
            if (command.getMinQuantity() != null) {
                bo.setMinQuantity(command.getMinQuantity());
            }
            if (command.getUnit() != null) {
                bo.setMinUnit(command.getUnit());
            }
        }
        return bo;
    }

    /**
     * Entity → Response
     */
    public static ProductResponse entityToResponse(ProductBO bo) {
        if (bo == null) {
            return null;
        }
        return ProductResponse.builder()
                .id(bo.getId())
                .productNo(bo.getProductNo())
                .name(bo.getName())
                .description(bo.getDescription())
                .price(bo.getPrice())
                .minQuantity(bo.getMinQuantity())
                .unit(bo.getMinUnit())
                .isPreset(bo.getIsPreset())
                .createTime(bo.getCreateTime())
                .updateTime(bo.getUpdateTime())
                .build();
    }
}
