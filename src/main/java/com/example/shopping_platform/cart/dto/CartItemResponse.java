package com.example.shopping_platform.cart.dto;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long productId,
        String name,
        String subtitle,
        BigDecimal price,
        BigDecimal originalPrice,
        Integer stock,
        String tag,
        String imageUrl,
        Integer quantity,
        boolean selected
) {
}
