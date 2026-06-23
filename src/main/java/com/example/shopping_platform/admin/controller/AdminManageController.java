package com.example.shopping_platform.admin.controller;

import com.example.shopping_platform.admin.dto.AdminOrderResponse;
import com.example.shopping_platform.admin.dto.AdminProductRequest;
import com.example.shopping_platform.admin.dto.AdminUserResponse;
import com.example.shopping_platform.admin.dto.ProductAvailabilityRequest;
import com.example.shopping_platform.admin.dto.ProductImageResponse;
import com.example.shopping_platform.admin.dto.ResetPasswordRequest;
import com.example.shopping_platform.admin.service.AdminService;
import com.example.shopping_platform.common.ApiResponse;
import com.example.shopping_platform.home.dto.ProductResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
public class AdminManageController {

    private final AdminService adminService;

    public AdminManageController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ApiResponse<List<AdminUserResponse>> users(@RequestParam(defaultValue = "") String keyword) {
        return ApiResponse.ok(adminService.listUsers(keyword));
    }

    @GetMapping("/users/{userId}/orders")
    public ApiResponse<List<AdminOrderResponse>> userOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "all") String status
    ) {
        return ApiResponse.ok(adminService.listUserOrders(userId, status));
    }

    @PostMapping("/users/{userId}/reset-password")
    public ApiResponse<AdminUserResponse> resetPassword(
            @PathVariable Long userId,
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        return ApiResponse.ok(adminService.resetPassword(userId, request));
    }

    @GetMapping("/orders")
    public ApiResponse<List<AdminOrderResponse>> orders(
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "") String keyword
    ) {
        return ApiResponse.ok(adminService.listOrders(status, keyword));
    }

    @PostMapping("/orders/{orderId}/ship")
    public ApiResponse<AdminOrderResponse> shipOrder(@PathVariable Long orderId) {
        return ApiResponse.ok(adminService.shipOrder(orderId));
    }

    @GetMapping("/products")
    public ApiResponse<List<ProductResponse>> products(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "all") String status
    ) {
        return ApiResponse.ok(adminService.listProducts(keyword, categoryId, status));
    }

    @PostMapping("/products")
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody AdminProductRequest request) {
        return ApiResponse.ok(adminService.createProduct(request));
    }

    @PostMapping(value = "/products/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductImageResponse> uploadProductImage(@RequestPart("file") MultipartFile file) {
        return ApiResponse.ok(adminService.uploadProductImage(file));
    }

    @PutMapping("/products/{productId}")
    public ApiResponse<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody AdminProductRequest request
    ) {
        return ApiResponse.ok(adminService.updateProduct(productId, request));
    }

    @PatchMapping("/products/{productId}/availability")
    public ApiResponse<ProductResponse> updateProductAvailability(
            @PathVariable Long productId,
            @Valid @RequestBody ProductAvailabilityRequest request
    ) {
        return ApiResponse.ok(adminService.updateProductAvailability(productId, request));
    }
}
