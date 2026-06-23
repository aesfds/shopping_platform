package com.example.shopping_platform.admin.dto;

import java.math.BigDecimal;

public record AdminUserResponse(
        Long id,
        String username,
        String email,
        String phone,
        String avatarUrl,
        BigDecimal balance,
        String status,
        String createdAt
) {
}
