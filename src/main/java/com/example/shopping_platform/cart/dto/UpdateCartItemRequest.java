package com.example.shopping_platform.cart.dto;

import jakarta.validation.constraints.Min;

public record UpdateCartItemRequest(
        @Min(value = 1, message = "数量至少为 1")
        Integer quantity,
        Boolean selected
) {
}
