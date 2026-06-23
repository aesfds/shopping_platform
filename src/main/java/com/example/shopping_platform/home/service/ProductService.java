package com.example.shopping_platform.home.service;

import com.example.shopping_platform.common.BusinessException;
import com.example.shopping_platform.home.dto.ProductCategoryResponse;
import com.example.shopping_platform.home.dto.ProductPageResponse;
import com.example.shopping_platform.home.dto.ProductResponse;
import com.example.shopping_platform.home.entity.Product;
import com.example.shopping_platform.home.repository.ProductCategoryRepository;
import com.example.shopping_platform.home.repository.ProductRepository;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, ProductCategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<ProductCategoryResponse> categories() {
        return categoryRepository.findByEnabledTrueOrderBySortOrderAscIdAsc().stream()
                .map(category -> new ProductCategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getIcon(),
                        category.getTargetRoute()
                ))
                .toList();
    }

    public ProductPageResponse list(String keyword, Long categoryId, String sort, int page, int size) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        List<Product> filtered = (categoryId == null || categoryId == 0
                ? productRepository.findByAvailableTrueOrderBySortOrderAscIdAsc()
                : productRepository.findByCategoryIdAndAvailableTrueOrderBySortOrderAscIdAsc(categoryId))
                .stream()
                .filter(product -> normalizedKeyword.isBlank() || matches(product, normalizedKeyword))
                .sorted(comparator(sort))
                .toList();

        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 50);
        int start = Math.min((safePage - 1) * safeSize, filtered.size());
        int end = Math.min(start + safeSize, filtered.size());

        return new ProductPageResponse(
                filtered.subList(start, end).stream()
                        .map(product -> toResponse(product, List.of()))
                        .toList(),
                filtered.size(),
                safePage,
                safeSize
        );
    }

    public ProductResponse detail(Long id) {
        Product product = productRepository.findById(id)
                .filter(Product::isAvailable)
                .orElseThrow(() -> new BusinessException(404, "商品不存在或已下架", HttpStatus.NOT_FOUND));
        List<ProductResponse> related = productRepository
                .findByCategoryIdAndAvailableTrueOrderBySortOrderAscIdAsc(product.getCategoryId())
                .stream()
                .filter(item -> !item.getId().equals(product.getId()))
                .limit(4)
                .map(item -> toResponse(item, List.of()))
                .toList();
        return toResponse(product, related);
    }

    private boolean matches(Product product, String keyword) {
        return contains(product.getName(), keyword)
                || contains(product.getSubtitle(), keyword)
                || contains(product.getDescription(), keyword)
                || contains(product.getTag(), keyword);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private Comparator<Product> comparator(String sort) {
        return switch (sort == null ? "" : sort) {
            case "priceAsc" -> Comparator.comparing(Product::getPrice);
            case "priceDesc" -> Comparator.comparing(Product::getPrice).reversed();
            case "sales" -> Comparator.comparing(Product::getSales, Comparator.nullsLast(Long::compareTo)).reversed();
            default -> Comparator.comparing(Product::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                    .thenComparing(Product::getId);
        };
    }

    public ProductResponse toResponse(Product product, List<ProductResponse> related) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSubtitle(),
                product.getDescription(),
                product.getPrice(),
                product.getOriginalPrice(),
                product.getSales(),
                product.getStock(),
                product.getTag(),
                product.getImageUrl(),
                product.getCategoryId(),
                product.getSortOrder(),
                product.isAvailable(),
                splitSpecs(product.getSpecs()),
                related
        );
    }

    private List<String> splitSpecs(String specs) {
        if (specs == null || specs.isBlank()) {
            return List.of();
        }
        return Arrays.stream(specs.split("\\|"))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();
    }
}
