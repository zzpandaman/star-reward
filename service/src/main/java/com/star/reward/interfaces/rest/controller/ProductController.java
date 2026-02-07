package com.star.reward.interfaces.rest.controller;

import com.star.common.page.PageResponse;
import com.star.common.result.Result;
import com.star.reward.application.service.ProductApplicationService;
import com.star.reward.interfaces.rest.assembler.ProductRequestAssembler;
import com.star.reward.interfaces.rest.dto.request.CreateProductRequest;
import com.star.reward.interfaces.rest.dto.request.UpdateProductRequest;
import com.star.reward.interfaces.rest.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/api/reward/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductApplicationService productApplicationService;
    
    /**
     * 获取所有商品
     */
    @GetMapping
    public Result<PageResponse<ProductResponse>> getAllProducts() {
        PageResponse<ProductResponse> response = productApplicationService.getAllProducts();
        return Result.success(response);
    }
    
    /**
     * 根据ID获取商品
     */
    @GetMapping("/{id}")
    public Result<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse response = productApplicationService.getProductById(id);
        return Result.success(response);
    }
    
    /**
     * 创建商品
     */
    @PostMapping
    public Result<ProductResponse> createProduct(@Validated @RequestBody CreateProductRequest request) {
        ProductResponse response = productApplicationService.createProduct(ProductRequestAssembler.requestToCreateCommand(request));
        return Result.success(response);
    }
    
    /**
     * 更新商品
     */
    @PutMapping("/{id}")
    public Result<ProductResponse> updateProduct(
            @PathVariable Long id,
            @RequestBody UpdateProductRequest request) {
        ProductResponse response = productApplicationService.updateProduct(id, ProductRequestAssembler.requestToUpdateCommand(request));
        return Result.success(response);
    }
    
    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productApplicationService.deleteProduct(id);
        return Result.success();
    }
}
