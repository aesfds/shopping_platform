package com.example.shopping_platform.admin.controller;

import com.example.shopping_platform.admin.dto.AdminAuthResponse;
import com.example.shopping_platform.admin.dto.AdminLoginRequest;
import com.example.shopping_platform.admin.dto.AdminProfileResponse;
import com.example.shopping_platform.admin.service.AdminService;
import com.example.shopping_platform.auth.security.AuthInterceptor;
import com.example.shopping_platform.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    private final AdminService adminService;

    public AdminAuthController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public ApiResponse<AdminAuthResponse> login(@Valid @RequestBody AdminLoginRequest request) {
        return ApiResponse.ok(adminService.login(request));
    }

    @GetMapping("/profile")
    public ApiResponse<AdminProfileResponse> profile() {
        return ApiResponse.ok(adminService.profile());
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        adminService.logout(AuthInterceptor.extractToken(request));
        return ApiResponse.ok(null);
    }
}
