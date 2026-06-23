package com.example.shopping_platform.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddCartItemRequest(
        @NotNull(message = "请选择商品")
        Long productId,

        @NotNull(message = "请输入数量")
        @Min(value = 1, message = "数量至少为 1")
        Integer quantity
) {
}
