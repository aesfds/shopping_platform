package com.example.shopping_platform.home.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String subtitle,
        String description,
        BigDecimal price,
        BigDecimal originalPrice,
        Long sales,
        Integer stock,
        String tag,
        String imageUrl,
        Long categoryId,
        Integer sortOrder,
        boolean available,
        List<String> specs,
        List<ProductResponse> related
) {
}
