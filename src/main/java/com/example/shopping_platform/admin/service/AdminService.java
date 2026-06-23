package com.example.shopping_platform.admin.service;

import com.example.shopping_platform.admin.dto.AdminAuthResponse;
import com.example.shopping_platform.admin.dto.AdminLoginRequest;
import com.example.shopping_platform.admin.dto.AdminOrderResponse;
import com.example.shopping_platform.admin.dto.AdminProductRequest;
import com.example.shopping_platform.admin.dto.AdminProfileResponse;
import com.example.shopping_platform.admin.dto.AdminUserResponse;
import com.example.shopping_platform.admin.dto.ProductAvailabilityRequest;
import com.example.shopping_platform.admin.dto.ProductImageResponse;
import com.example.shopping_platform.admin.dto.ResetPasswordRequest;
import com.example.shopping_platform.auth.entity.UserAccount;
import com.example.shopping_platform.auth.repository.UserAccountRepository;
import com.example.shopping_platform.auth.security.PasswordHasher;
import com.example.shopping_platform.auth.security.TokenHasher;
import com.example.shopping_platform.common.BusinessException;
import com.example.shopping_platform.home.dto.ProductResponse;
import com.example.shopping_platform.home.entity.Product;
import com.example.shopping_platform.home.repository.ProductCategoryRepository;
import com.example.shopping_platform.home.repository.ProductRepository;
import com.example.shopping_platform.home.service.ProductService;
import com.example.shopping_platform.order.dto.OrderItemResponse;
import com.example.shopping_platform.order.entity.UserOrder;
import com.example.shopping_platform.order.repository.UserOrderRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AdminService {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "123456";
    private static final String ADMIN_ROLE = "ADMIN";
    private static final long SESSION_HOURS = 8;
    private static final long MAX_PRODUCT_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    private final Map<String, LocalDateTime> adminSessions = new ConcurrentHashMap<>();
    private final TokenHasher tokenHasher;
    private final PasswordHasher passwordHasher;
    private final UserAccountRepository userRepository;
    private final UserOrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ProductService productService;

    @Value("${shopping.upload.product-dir:./uploads/products}")
    private String productImageDir;

    public AdminService(
            TokenHasher tokenHasher,
            PasswordHasher passwordHasher,
            UserAccountRepository userRepository,
            UserOrderRepository orderRepository,
            ProductRepository productRepository,
            ProductCategoryRepository categoryRepository,
            ProductService productService
    ) {
        this.tokenHasher = tokenHasher;
        this.passwordHasher = passwordHasher;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productService = productService;
    }

    public AdminAuthResponse login(AdminLoginRequest request) {
        if (!ADMIN_USERNAME.equals(request.username().trim()) || !ADMIN_PASSWORD.equals(request.password())) {
            throw new BusinessException(401, "管理员账号或密码错误", HttpStatus.UNAUTHORIZED);
        }

        String token = tokenHasher.generateToken();
        adminSessions.put(tokenHasher.hash(token), LocalDateTime.now().plusHours(SESSION_HOURS));
        return new AdminAuthResponse(token, profile());
    }

    public boolean authenticate(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        LocalDateTime expiresAt = adminSessions.get(tokenHasher.hash(token));
        return expiresAt != null && expiresAt.isAfter(LocalDateTime.now());
    }

    public void logout(String token) {
        if (token != null && !token.isBlank()) {
            adminSessions.remove(tokenHasher.hash(token));
        }
    }

    public AdminProfileResponse profile() {
        return new AdminProfileResponse(ADMIN_USERNAME, ADMIN_ROLE);
    }

    @Transactional(readOnly = true)
    public List<AdminUserResponse> listUsers(String keyword) {
        String normalized = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        return userRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(user -> normalized.isBlank() || matchesUser(user, normalized))
                .map(this::toUserResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdminOrderResponse> listOrders(String status, String keyword) {
        List<UserOrder> orders = status == null || status.isBlank() || "all".equals(status)
                ? orderRepository.findAllByOrderByCreatedAtDesc()
                : orderRepository.findByStatusOrderByCreatedAtDesc(status);
        String normalized = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);

        return orders.stream()
                .filter(order -> normalized.isBlank() || matchesOrder(order, normalized))
                .map(this::toOrderResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdminOrderResponse> listUserOrders(Long userId, String status) {
        List<UserOrder> orders = status == null || status.isBlank() || "all".equals(status)
                ? orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                : orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
        return orders.stream().map(this::toOrderResponse).toList();
    }

    @Transactional
    public AdminUserResponse resetPassword(Long userId, ResetPasswordRequest request) {
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在", HttpStatus.NOT_FOUND));
        user.resetPassword(passwordHasher.hash(request.newPassword()));
        return toUserResponse(user);
    }

    @Transactional
    public AdminOrderResponse shipOrder(Long orderId) {
        UserOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(404, "订单不存在", HttpStatus.NOT_FOUND));
        if (!"pendingShipment".equals(order.getStatus())) {
            throw new BusinessException(400, "只有待发货订单可以发货");
        }

        order.ship();
        return toOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> listProducts(String keyword, Long categoryId, String status) {
        String normalized = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        return productRepository.findAllByOrderBySortOrderAscIdAsc().stream()
                .filter(product -> categoryId == null || categoryId == 0 || product.getCategoryId().equals(categoryId))
                .filter(product -> matchesProductStatus(product, status))
                .filter(product -> normalized.isBlank() || matchesProduct(product, normalized))
                .map(product -> productService.toResponse(product, List.of()))
                .toList();
    }

    @Transactional
    public ProductResponse createProduct(AdminProductRequest request) {
        validateCategory(request.categoryId());
        Product product = new Product(
                request.name().trim(),
                request.subtitle().trim(),
                trimToEmpty(request.description()),
                request.price(),
                request.originalPrice(),
                request.sales(),
                trimToEmpty(request.tag()),
                trimToEmpty(request.imageUrl()),
                request.stock(),
                joinSpecs(request.specs()),
                request.categoryId(),
                request.sortOrder()
        );
        if (request.available() != null && !request.available()) {
            product.updateAvailable(false);
        }
        return productService.toResponse(productRepository.save(product), List.of());
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, AdminProductRequest request) {
        validateCategory(request.categoryId());
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(404, "商品不存在", HttpStatus.NOT_FOUND));
        product.update(
                request.name().trim(),
                request.subtitle().trim(),
                trimToEmpty(request.description()),
                request.price(),
                request.originalPrice(),
                request.sales(),
                trimToEmpty(request.tag()),
                trimToEmpty(request.imageUrl()),
                request.stock(),
                joinSpecs(request.specs()),
                request.categoryId(),
                request.sortOrder(),
                request.available() == null || request.available()
        );
        return productService.toResponse(product, List.of());
    }

    @Transactional
    public ProductResponse updateProductAvailability(Long productId, ProductAvailabilityRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(404, "商品不存在", HttpStatus.NOT_FOUND));
        product.updateAvailable(request.available());
        return productService.toResponse(product, List.of());
    }

    public ProductImageResponse uploadProductImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "请选择商品图片");
        }
        if (file.getSize() > MAX_PRODUCT_IMAGE_SIZE) {
            throw new BusinessException(400, "商品图片不能超过 5MB");
        }

        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new BusinessException(400, "商品图片仅支持 JPG、PNG、GIF、WEBP 格式");
        }

        Path uploadPath = Path.of(productImageDir).toAbsolutePath().normalize();
        String filename = "product-" + UUID.randomUUID() + extensionFor(contentType);
        Path target = uploadPath.resolve(filename).normalize();
        if (!target.startsWith(uploadPath)) {
            throw new BusinessException(400, "图片文件名不合法");
        }

        try {
            Files.createDirectories(uploadPath);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new BusinessException(500, "商品图片上传失败", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ProductImageResponse("/api/uploads/products/" + filename);
    }

    private boolean matchesUser(UserAccount user, String keyword) {
        return contains(user.getUsername(), keyword)
                || contains(user.getEmail(), keyword)
                || contains(user.getPhone(), keyword)
                || String.valueOf(user.getId()).contains(keyword);
    }

    private boolean matchesOrder(UserOrder order, String keyword) {
        UserAccount user = order.getUser();
        return contains(order.getOrderNo(), keyword)
                || contains(user.getUsername(), keyword)
                || contains(user.getPhone(), keyword)
                || String.valueOf(order.getId()).contains(keyword)
                || String.valueOf(user.getId()).contains(keyword);
    }

    private boolean matchesProduct(Product product, String keyword) {
        return contains(product.getName(), keyword)
                || contains(product.getSubtitle(), keyword)
                || contains(product.getDescription(), keyword)
                || contains(product.getTag(), keyword)
                || String.valueOf(product.getId()).contains(keyword);
    }

    private boolean matchesProductStatus(Product product, String status) {
        if (status == null || status.isBlank() || "all".equals(status)) {
            return true;
        }
        if ("available".equals(status)) {
            return product.isAvailable();
        }
        if ("offline".equals(status)) {
            return !product.isAvailable();
        }
        return true;
    }

    private void validateCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new BusinessException(400, "商品分类不存在");
        }
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String joinSpecs(List<String> specs) {
        if (specs == null) {
            return "";
        }
        return specs.stream()
                .map(this::trimToEmpty)
                .filter(value -> !value.isBlank())
                .collect(Collectors.joining("|"));
    }

    private String extensionFor(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> ".bin";
        };
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private AdminUserResponse toUserResponse(UserAccount user) {
        return new AdminUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getAvatarUrl(),
                user.getBalance(),
                user.getStatus(),
                user.getCreatedAt().format(DATE_TIME_FORMATTER)
        );
    }

    private AdminOrderResponse toOrderResponse(UserOrder order) {
        UserAccount user = order.getUser();
        return new AdminOrderResponse(
                order.getId(),
                order.getOrderNo(),
                user.getId(),
                user.getUsername(),
                user.getPhone(),
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
}
