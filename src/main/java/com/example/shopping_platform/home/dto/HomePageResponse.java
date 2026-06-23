package com.example.shopping_platform.home.dto;

import java.math.BigDecimal;
import java.util.List;

public record HomePageResponse(
        List<BannerItem> banners,
        List<CategoryItem> categories,
        List<ProductSection> productSections,
        List<NavLink> navLinks
) {

    public record BannerItem(
            Long id,
            String title,
            String subtitle,
            String ctaText,
            String backgroundColor
    ) {
    }

    public record CategoryItem(
            Long id,
            String name,
            String icon,
            String targetRoute
    ) {
    }

    public record ProductItem(
            Long id,
            String name,
            String subtitle,
            BigDecimal price,
            BigDecimal originalPrice,
            Long sales,
            String tag,
            String imageUrl,
            Long categoryId,
            Integer stock
    ) {
    }

    public record ProductSection(
            String title,
            String subtitle,
            List<ProductItem> products
    ) {
    }

    public record NavLink(
            String label,
            String route,
            boolean reserved
    ) {
    }
}
