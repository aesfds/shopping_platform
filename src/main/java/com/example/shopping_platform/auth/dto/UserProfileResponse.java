package com.example.shopping_platform.auth.dto;

import java.math.BigDecimal;

public record UserProfileResponse(
        Long id,
        String username,
        String email,
        String phone,
        String avatarUrl,
        BigDecimal balance
) {
}
