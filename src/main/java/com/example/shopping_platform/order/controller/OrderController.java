package com.example.shopping_platform.order.controller;

import com.example.shopping_platform.auth.security.CurrentUser;
import com.example.shopping_platform.common.ApiResponse;
import com.example.shopping_platform.order.dto.OrderPaymentResponse;
import com.example.shopping_platform.order.dto.OrderResponse;
import com.example.shopping_platform.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ApiResponse<List<OrderResponse>> list(
            HttpServletRequest request,
            @RequestParam(defaultValue = "all") String status
    ) {
        return ApiResponse.ok(orderService.list(CurrentUser.require(request), status));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> detail(HttpServletRequest request, @PathVariable Long id) {
        return ApiResponse.ok(orderService.detail(CurrentUser.require(request), id));
    }

    @PostMapping
    public ApiResponse<OrderResponse> create(HttpServletRequest request) {
        return ApiResponse.ok(orderService.createFromSelectedCart(CurrentUser.require(request)));
    }

    @PostMapping("/{id}/pay")
    public ApiResponse<OrderPaymentResponse> pay(HttpServletRequest request, @PathVariable Long id) {
        return ApiResponse.ok(orderService.pay(CurrentUser.require(request), id));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<OrderResponse> cancel(HttpServletRequest request, @PathVariable Long id) {
        return ApiResponse.ok(orderService.cancel(CurrentUser.require(request), id));
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<OrderResponse> complete(HttpServletRequest request, @PathVariable Long id) {
        return ApiResponse.ok(orderService.complete(CurrentUser.require(request), id));
    }
}
