package com.example.shopping_platform.auth.dto;

public record AuthResponse(
        String token,
        UserProfileResponse user
) {
}
