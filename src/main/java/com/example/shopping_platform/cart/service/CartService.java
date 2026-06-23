package com.example.shopping_platform.cart.service;

import com.example.shopping_platform.auth.entity.UserAccount;
import com.example.shopping_platform.cart.dto.AddCartItemRequest;
import com.example.shopping_platform.cart.dto.CartItemResponse;
import com.example.shopping_platform.cart.dto.UpdateCartItemRequest;
import com.example.shopping_platform.cart.entity.CartItem;
import com.example.shopping_platform.cart.repository.CartItemRepository;
import com.example.shopping_platform.common.BusinessException;
import com.example.shopping_platform.home.entity.Product;
import com.example.shopping_platform.home.repository.ProductRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse> list(UserAccount user) {
        return cartItemRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public List<CartItemResponse> add(UserAccount user, AddCartItemRequest request) {
        Product product = productRepository.findById(request.productId())
                .filter(Product::isAvailable)
                .orElseThrow(() -> new BusinessException(404, "商品不存在或已下架", HttpStatus.NOT_FOUND));

        CartItem item = cartItemRepository.findByUserIdAndProductId(user.getId(), product.getId())
                .orElseGet(() -> new CartItem(user, product, 0));
        item.setQuantity(Math.min(product.getStock(), item.getQuantity() + request.quantity()));
        item.setSelected(true);
        cartItemRepository.save(item);
        return list(user);
    }

    @Transactional
    public List<CartItemResponse> update(UserAccount user, Long productId, UpdateCartItemRequest request) {
        CartItem item = cartItemRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElseThrow(() -> new BusinessException(404, "购物车商品不存在", HttpStatus.NOT_FOUND));

        if (request.quantity() != null) {
            item.setQuantity(Math.min(item.getProduct().getStock(), request.quantity()));
        }
        if (request.selected() != null) {
            item.setSelected(request.selected());
        }

        return list(user);
    }

    @Transactional
    public List<CartItemResponse> remove(UserAccount user, Long productId) {
        cartItemRepository.deleteByUserIdAndProductId(user.getId(), productId);
        return list(user);
    }

    @Transactional
    public List<CartItemResponse> removeSelected(UserAccount user) {
        cartItemRepository.deleteByUserIdAndSelectedTrue(user.getId());
        return list(user);
    }

    private CartItemResponse toResponse(CartItem item) {
        Product product = item.getProduct();
        return new CartItemResponse(
                item.getId(),
                product.getId(),
                product.getName(),
                product.getSubtitle(),
                product.getPrice(),
                product.getOriginalPrice(),
                product.getStock(),
                product.getTag(),
                product.getImageUrl(),
                item.getQuantity(),
                item.isSelected()
        );
    }
}
