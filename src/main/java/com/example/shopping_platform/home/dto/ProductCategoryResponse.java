package com.example.shopping_platform.home.dto;

public record ProductCategoryResponse(
        Long id,
        String name,
        String icon,
        String targetRoute
) {
}
