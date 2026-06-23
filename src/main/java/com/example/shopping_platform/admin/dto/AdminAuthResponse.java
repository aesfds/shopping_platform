package com.example.shopping_platform.admin.dto;

public record AdminAuthResponse(
        String token,
        AdminProfileResponse admin
) {
}
