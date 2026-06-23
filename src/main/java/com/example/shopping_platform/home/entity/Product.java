package com.example.shopping_platform.home.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 160)
    private String subtitle;

    @Column(length = 800)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;

    private Long sales;

    @Column(length = 32)
    private String tag;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    private Integer stock;

    @Column(length = 500)
    private String specs;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(nullable = false)
    private boolean available;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Product() {
    }

    public Product(
            String name,
            String subtitle,
            String description,
            BigDecimal price,
            BigDecimal originalPrice,
            Long sales,
            String tag,
            String imageUrl,
            Integer stock,
            String specs,
            Long categoryId,
            Integer sortOrder
    ) {
        this.name = name;
        this.subtitle = subtitle;
        this.description = description;
        this.price = price;
        this.originalPrice = originalPrice;
        this.sales = sales == null ? 0L : sales;
        this.tag = tag;
        this.imageUrl = imageUrl;
        this.stock = stock == null ? 99 : stock;
        this.specs = specs;
        this.categoryId = categoryId;
        this.sortOrder = sortOrder == null ? 0 : sortOrder;
        this.available = true;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public Long getSales() {
        return sales == null ? 0L : sales;
    }

    public String getTag() {
        return tag;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getStock() {
        return stock == null ? 99 : stock;
    }

    public String getSpecs() {
        return specs;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public boolean isAvailable() {
        return available;
    }

    public void updateAvailable(boolean available) {
        this.available = available;
    }

    public void increaseSales(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            return;
        }
        this.sales = getSales() + quantity;
    }

    public void update(
            String name,
            String subtitle,
            String description,
            BigDecimal price,
            BigDecimal originalPrice,
            Long sales,
            String tag,
            String imageUrl,
            Integer stock,
            String specs,
            Long categoryId,
            Integer sortOrder,
            boolean available
    ) {
        this.name = name;
        this.subtitle = subtitle;
        this.description = description;
        this.price = price;
        this.originalPrice = originalPrice;
        this.sales = sales == null ? 0L : sales;
        this.tag = tag;
        this.imageUrl = imageUrl;
        this.stock = stock == null ? 99 : stock;
        this.specs = specs;
        this.categoryId = categoryId;
        this.sortOrder = sortOrder == null ? 0 : sortOrder;
        this.available = available;
    }
}
