package com.example.shopping_platform.order.service;

import com.example.shopping_platform.auth.entity.UserAccount;
import com.example.shopping_platform.auth.repository.UserAccountRepository;
import com.example.shopping_platform.cart.entity.CartItem;
import com.example.shopping_platform.cart.repository.CartItemRepository;
import com.example.shopping_platform.common.BusinessException;
import com.example.shopping_platform.home.entity.Product;
import com.example.shopping_platform.home.repository.ProductRepository;
import com.example.shopping_platform.order.dto.OrderItemResponse;
import com.example.shopping_platform.order.dto.OrderPaymentResponse;
import com.example.shopping_platform.order.dto.OrderResponse;
import com.example.shopping_platform.order.entity.OrderItem;
import com.example.shopping_platform.order.entity.UserOrder;
import com.example.shopping_platform.order.repository.UserOrderRepository;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final UserOrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserAccountRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(
            UserOrderRepository orderRepository,
            CartItemRepository cartItemRepository,
            UserAccountRepository userRepository,
            ProductRepository productRepository
    ) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> list(UserAccount user, String status) {
        List<UserOrder> orders = status == null || status.isBlank() || "all".equals(status)
                ? orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                : orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(user.getId(), status);
        return orders.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse detail(UserAccount user, Long id) {
        return orderRepository.findByUserIdAndId(user.getId(), id)
                .map(this::toResponse)
                .orElseThrow(() -> new BusinessException(404, "订单不存在", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public OrderResponse createFromSelectedCart(UserAccount user) {
        List<CartItem> selectedItems = cartItemRepository.findByUserIdAndSelectedTrueOrderByCreatedAtDesc(user.getId());
        if (selectedItems.isEmpty()) {
            throw new BusinessException(400, "请选择要结算的商品");
        }

        BigDecimal totalAmount = selectedItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        UserOrder order = new UserOrder(generateOrderNo(), user, totalAmount);
        selectedItems.forEach(item -> {
            Product product = item.getProduct();
            order.addItem(new OrderItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    item.getQuantity()
            ));
        });

        UserOrder savedOrder = orderRepository.save(order);
        cartItemRepository.deleteByUserIdAndSelectedTrue(user.getId());
        return toResponse(savedOrder);
    }

    @Transactional
    public OrderPaymentResponse pay(UserAccount currentUser, Long id) {
        UserAccount user = findFreshUser(currentUser);
        UserOrder order = findOwnOrder(user, id);

        if (!"pendingPay".equals(order.getStatus())) {
            throw new BusinessException(400, "只有待支付订单可以支付");
        }
        if (user.getBalance().compareTo(order.getTotalAmount()) < 0) {
            throw new BusinessException(400, "余额不足，请先充值");
        }

        user.deductBalance(order.getTotalAmount());
        order.markPaid();
        return new OrderPaymentResponse(user.getBalance(), toResponse(order));
    }

    @Transactional
    public OrderResponse cancel(UserAccount user, Long id) {
        UserOrder order = findOwnOrder(user, id);
        if (!"pendingPay".equals(order.getStatus())) {
            throw new BusinessException(400, "只有待支付订单可以取消");
        }

        order.cancel();
        return toResponse(order);
    }

    @Transactional
    public OrderResponse complete(UserAccount user, Long id) {
        UserOrder order = findOwnOrder(user, id);
        if (!"shipped".equals(order.getStatus())) {
            throw new BusinessException(400, "只有已发货订单可以确认完成");
        }

        order.getItems().forEach(item -> productRepository.findById(item.getProductId())
                .ifPresent(product -> product.increaseSales(item.getQuantity())));
        order.complete();
        return toResponse(order);
    }

    private UserAccount findFreshUser(UserAccount currentUser) {
        return userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new BusinessException(404, "用户不存在", HttpStatus.NOT_FOUND));
    }

    private UserOrder findOwnOrder(UserAccount user, Long id) {
        return orderRepository.findByUserIdAndId(user.getId(), id)
                .orElseThrow(() -> new BusinessException(404, "订单不存在", HttpStatus.NOT_FOUND));
    }

    private OrderResponse toResponse(UserOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNo(),
                order.getStatus(),
                order.getCreatedAt().format(DATE_TIME_FORMATTER),
                order.getTotalAmount(),
                order.getReceiverName(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getProductId(),
                                item.getProductName(),
                                item.getPrice(),
                                item.getQuantity(),
                                item.getSubtotal()
                        ))
                        .toList()
        );
    }

    private String generateOrderNo() {
        return "SP" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
