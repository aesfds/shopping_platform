package com.example.shopping_platform.order.repository;

import com.example.shopping_platform.order.entity.UserOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOrderRepository extends JpaRepository<UserOrder, Long> {

    List<UserOrder> findAllByOrderByCreatedAtDesc();

    List<UserOrder> findByStatusOrderByCreatedAtDesc(String status);

    List<UserOrder> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<UserOrder> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);

    Optional<UserOrder> findByUserIdAndId(Long userId, Long id);
}
