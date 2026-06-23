package com.example.shopping_platform.auth.controller;

import com.example.shopping_platform.auth.dto.AuthResponse;
import com.example.shopping_platform.auth.dto.LoginRequest;
import com.example.shopping_platform.auth.dto.RechargeRequest;
import com.example.shopping_platform.auth.dto.RegisterRequest;
import com.example.shopping_platform.auth.dto.UpdateProfileRequest;
import com.example.shopping_platform.auth.dto.UserProfileResponse;
import com.example.shopping_platform.auth.security.CurrentUser;
import com.example.shopping_platform.auth.service.AuthService;
import com.example.shopping_platform.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestPart;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> profile(HttpServletRequest request) {
        return ApiResponse.ok(authService.toProfile(CurrentUser.require(request)));
    }

    @PutMapping("/profile")
    public ApiResponse<UserProfileResponse> updateProfile(
            HttpServletRequest request,
            @Valid @RequestBody UpdateProfileRequest updateRequest
    ) {
        return ApiResponse.ok(authService.updateProfile(CurrentUser.require(request), updateRequest));
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserProfileResponse> uploadAvatar(
            HttpServletRequest request,
            @RequestPart("file") MultipartFile file
    ) {
        return ApiResponse.ok(authService.uploadAvatar(CurrentUser.require(request), file));
    }

    @PostMapping("/recharge")
    public ApiResponse<UserProfileResponse> recharge(
            HttpServletRequest request,
            @Valid @RequestBody RechargeRequest rechargeRequest
    ) {
        return ApiResponse.ok(authService.recharge(CurrentUser.require(request), rechargeRequest));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        authService.logout(CurrentUser.requireToken(request));
        return ApiResponse.ok(null);
    }
}
