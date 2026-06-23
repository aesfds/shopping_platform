package com.example.shopping_platform.admin.dto;

import jakarta.validation.constraints.NotNull;

public record ProductAvailabilityRequest(
        @NotNull(message = "请选择商品状态")
        Boolean available
) {
}
