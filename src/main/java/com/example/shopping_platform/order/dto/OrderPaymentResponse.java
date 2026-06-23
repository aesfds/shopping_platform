package com.example.shopping_platform.order.dto;

import java.math.BigDecimal;

public record OrderPaymentResponse(
        BigDecimal balance,
        OrderResponse order
) {
}
