package com.star.reward.interfaces.rest.assembler;

import com.star.reward.application.command.CreateProductCommand;
import com.star.reward.application.command.UpdateProductCommand;
import com.star.reward.interfaces.rest.dto.request.CreateProductRequest;
import com.star.reward.interfaces.rest.dto.request.UpdateProductRequest;

/**
 * 商品 Request → Command 转换器（Interfaces → Application 边界）
 */
public final class ProductRequestAssembler {

    private ProductRequestAssembler() {
    }

    public static CreateProductCommand requestToCreateCommand(CreateProductRequest request) {
        if (request == null) {
            return null;
        }
        CreateProductCommand cmd = new CreateProductCommand();
        cmd.setName(request.getName());
        cmd.setDescription(request.getDescription());
        cmd.setPrice(request.getPrice());
        cmd.setMinQuantity(request.getMinQuantity());
        cmd.setUnit(request.getUnit());
        return cmd;
    }

    public static UpdateProductCommand requestToUpdateCommand(UpdateProductRequest request) {
        if (request == null) {
            return null;
        }
        UpdateProductCommand cmd = new UpdateProductCommand();
        cmd.setName(request.getName());
        cmd.setDescription(request.getDescription());
        cmd.setPrice(request.getPrice());
        cmd.setMinQuantity(request.getMinQuantity());
        cmd.setUnit(request.getUnit());
        return cmd;
    }
}
