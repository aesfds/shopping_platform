package com.example.shopping_platform.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long productId,
        String name,
        BigDecimal price,
        Integer quantity,
        BigDecimal subtotal
) {
}
