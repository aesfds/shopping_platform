package com.example.shopping_platform.home.repository;

import com.example.shopping_platform.home.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findTop8ByAvailableTrueOrderBySortOrderAscIdAsc();

    List<Product> findTop8ByAvailableTrueOrderByCreatedAtDescIdDesc();

    List<Product> findByAvailableTrueOrderBySortOrderAscIdAsc();

    List<Product> findByCategoryIdAndAvailableTrueOrderBySortOrderAscIdAsc(Long categoryId);

    List<Product> findByAvailableOrderBySortOrderAscIdAsc(boolean available);

    List<Product> findAllByOrderBySortOrderAscIdAsc();
}
