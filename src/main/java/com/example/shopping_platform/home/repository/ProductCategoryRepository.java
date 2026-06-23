package com.example.shopping_platform.home.repository;

import com.example.shopping_platform.home.entity.ProductCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    List<ProductCategory> findByEnabledTrueOrderBySortOrderAscIdAsc();
}
