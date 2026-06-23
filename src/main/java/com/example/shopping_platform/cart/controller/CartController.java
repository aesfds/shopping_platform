package com.example.shopping_platform.cart.controller;

import com.example.shopping_platform.auth.security.CurrentUser;
import com.example.shopping_platform.cart.dto.AddCartItemRequest;
import com.example.shopping_platform.cart.dto.CartItemResponse;
import com.example.shopping_platform.cart.dto.UpdateCartItemRequest;
import com.example.shopping_platform.cart.service.CartService;
import com.example.shopping_platform.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ApiResponse<List<CartItemResponse>> list(HttpServletRequest request) {
        return ApiResponse.ok(cartService.list(CurrentUser.require(request)));
    }

    @PostMapping("/items")
    public ApiResponse<List<CartItemResponse>> add(
            HttpServletRequest request,
            @Valid @RequestBody AddCartItemRequest body
    ) {
        return ApiResponse.ok(cartService.add(CurrentUser.require(request), body));
    }

    @PutMapping("/items/{productId}")
    public ApiResponse<List<CartItemResponse>> update(
            HttpServletRequest request,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest body
    ) {
        return ApiResponse.ok(cartService.update(CurrentUser.require(request), productId, body));
    }

    @DeleteMapping("/items/{productId}")
    public ApiResponse<List<CartItemResponse>> remove(HttpServletRequest request, @PathVariable Long productId) {
        return ApiResponse.ok(cartService.remove(CurrentUser.require(request), productId));
    }

    @DeleteMapping("/items/selected")
    public ApiResponse<List<CartItemResponse>> removeSelected(HttpServletRequest request) {
        return ApiResponse.ok(cartService.removeSelected(CurrentUser.require(request)));
    }
}
