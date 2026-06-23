package com.example.shopping_platform.admin.dto;

import com.example.shopping_platform.order.dto.OrderItemResponse;
import java.math.BigDecimal;
import java.util.List;

public record AdminOrderResponse(
        Long id,
        String orderNo,
        Long userId,
        String username,
        String phone,
        String status,
        String createdAt,
        BigDecimal totalAmount,
        String receiver,
        List<OrderItemResponse> items
) {
}
