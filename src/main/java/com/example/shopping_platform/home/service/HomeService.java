package com.example.shopping_platform.home.service;

import com.example.shopping_platform.home.dto.HomePageResponse;
import com.example.shopping_platform.home.entity.HomeBanner;
import com.example.shopping_platform.home.entity.Product;
import com.example.shopping_platform.home.entity.ProductCategory;
import com.example.shopping_platform.home.repository.HomeBannerRepository;
import com.example.shopping_platform.home.repository.ProductCategoryRepository;
import com.example.shopping_platform.home.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class HomeService {

    private final HomeBannerRepository bannerRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public HomeService(
            HomeBannerRepository bannerRepository,
            ProductCategoryRepository categoryRepository,
            ProductRepository productRepository
    ) {
        this.bannerRepository = bannerRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public HomePageResponse getHomePage() {
        return new HomePageResponse(
                bannerRepository.findByEnabledTrueOrderBySortOrderAscIdAsc().stream()
                        .map(this::toBannerItem)
                        .toList(),
                categoryRepository.findByEnabledTrueOrderBySortOrderAscIdAsc().stream()
                        .map(this::toCategoryItem)
                        .toList(),
                List.of(
                        new HomePageResponse.ProductSection(
                                "热门推荐",
                                "大家都在买",
                                productRepository.findTop8ByAvailableTrueOrderBySortOrderAscIdAsc().stream()
                                        .map(this::toProductItem)
                                        .toList()
                        ),
                        new HomePageResponse.ProductSection(
                                "新品上架",
                                "最近上架好物",
                                productRepository.findTop8ByAvailableTrueOrderByCreatedAtDescIdDesc().stream()
                                        .map(this::toProductItem)
                                        .toList()
                        )
                ),
                List.of(
                        new HomePageResponse.NavLink("首页", "/", false),
                        new HomePageResponse.NavLink("全部商品", "/products", true),
                        new HomePageResponse.NavLink("购物车", "/cart", true),
                        new HomePageResponse.NavLink("我的订单", "/orders", true),
                        new HomePageResponse.NavLink("登录", "/login", true)
                )
        );
    }

    private HomePageResponse.BannerItem toBannerItem(HomeBanner banner) {
        return new HomePageResponse.BannerItem(
                banner.getId(),
                banner.getTitle(),
                banner.getSubtitle(),
                banner.getCtaText(),
                banner.getBackgroundColor()
        );
    }

    private HomePageResponse.CategoryItem toCategoryItem(ProductCategory category) {
        return new HomePageResponse.CategoryItem(
                category.getId(),
                category.getName(),
                category.getIcon(),
                category.getTargetRoute()
        );
    }

    private HomePageResponse.ProductItem toProductItem(Product product) {
        return new HomePageResponse.ProductItem(
                product.getId(),
                product.getName(),
                product.getSubtitle(),
                product.getPrice(),
                product.getOriginalPrice(),
                product.getSales(),
                product.getTag(),
                product.getImageUrl(),
                product.getCategoryId(),
                product.getStock()
        );
    }
}
