package com.example.shopping_platform.home.dto;

import java.util.List;

public record ProductPageResponse(
        List<ProductResponse> records,
        int total,
        int page,
        int size
) {
}
