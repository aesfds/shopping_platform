package com.example.shopping_platform.home.controller;

import com.example.shopping_platform.common.ApiResponse;
import com.example.shopping_platform.home.dto.ProductCategoryResponse;
import com.example.shopping_platform.home.dto.ProductPageResponse;
import com.example.shopping_platform.home.dto.ProductResponse;
import com.example.shopping_platform.home.service.ProductService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<ProductCategoryResponse>> categories() {
        return ApiResponse.ok(productService.categories());
    }

    @GetMapping
    public ApiResponse<ProductPageResponse> list(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "recommend") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size
    ) {
        return ApiResponse.ok(productService.list(keyword, categoryId, sort, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> detail(@PathVariable Long id) {
        return ApiResponse.ok(productService.detail(id));
    }
}
