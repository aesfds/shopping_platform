package com.example.shopping_platform.home.repository;

import com.example.shopping_platform.home.entity.HomeBanner;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeBannerRepository extends JpaRepository<HomeBanner, Long> {

    List<HomeBanner> findByEnabledTrueOrderBySortOrderAscIdAsc();
}
