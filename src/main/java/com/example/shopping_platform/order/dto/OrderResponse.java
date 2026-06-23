package com.example.shopping_platform.order.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNo,
        String status,
        String createdAt,
        BigDecimal totalAmount,
        String receiver,
        List<OrderItemResponse> items
) {
}
