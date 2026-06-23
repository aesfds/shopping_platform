package com.example.shopping_platform.auth.service;

import com.example.shopping_platform.auth.dto.AuthResponse;
import com.example.shopping_platform.auth.dto.LoginRequest;
import com.example.shopping_platform.auth.dto.RechargeRequest;
import com.example.shopping_platform.auth.dto.RegisterRequest;
import com.example.shopping_platform.auth.dto.UpdateProfileRequest;
import com.example.shopping_platform.auth.dto.UserProfileResponse;
import com.example.shopping_platform.auth.entity.AuthSession;
import com.example.shopping_platform.auth.entity.UserAccount;
import com.example.shopping_platform.auth.repository.AuthSessionRepository;
import com.example.shopping_platform.auth.repository.UserAccountRepository;
import com.example.shopping_platform.auth.security.PasswordHasher;
import com.example.shopping_platform.auth.security.TokenHasher;
import com.example.shopping_platform.common.BusinessException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AuthService {

    private static final long SESSION_DAYS = 7;
    private static final long MAX_AVATAR_SIZE = 2 * 1024 * 1024;
    private static final Set<String> ALLOWED_AVATAR_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    private final UserAccountRepository userRepository;
    private final AuthSessionRepository sessionRepository;
    private final PasswordHasher passwordHasher;
    private final TokenHasher tokenHasher;

    @Value("${shopping.upload.avatar-dir:./uploads/avatars}")
    private String avatarDir;

    public AuthService(
            UserAccountRepository userRepository,
            AuthSessionRepository sessionRepository,
            PasswordHasher passwordHasher,
            TokenHasher tokenHasher
    ) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.passwordHasher = passwordHasher;
        this.tokenHasher = tokenHasher;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String username = request.username().trim();
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String phone = request.phone().trim();

        if (!request.password().equals(request.confirmPassword())) {
            throw new BusinessException(400, "两次输入的密码不一致");
        }
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(409, "用户名已存在", HttpStatus.CONFLICT);
        }
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(409, "邮箱已被注册", HttpStatus.CONFLICT);
        }
        if (userRepository.existsByPhone(phone)) {
            throw new BusinessException(409, "手机号已被注册", HttpStatus.CONFLICT);
        }

        UserAccount user = userRepository.save(
                new UserAccount(username, email, phone, passwordHasher.hash(request.password()))
        );
        return createSessionResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        UserAccount user = findByAccount(request.account().trim())
                .orElseThrow(() -> new BusinessException(401, "账号或密码错误", HttpStatus.UNAUTHORIZED));

        if (!user.isActive() || !passwordHasher.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(401, "账号或密码错误", HttpStatus.UNAUTHORIZED);
        }

        return createSessionResponse(user);
    }

    @Transactional(readOnly = true)
    public Optional<UserAccount> authenticate(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return sessionRepository.findByTokenHashAndRevokedFalse(tokenHasher.hash(token))
                .filter(AuthSession::isUsable)
                .map(AuthSession::getUser);
    }

    @Transactional
    public void logout(String token) {
        if (token == null || token.isBlank()) {
            return;
        }

        sessionRepository.findByTokenHashAndRevokedFalse(tokenHasher.hash(token))
                .ifPresent(AuthSession::revoke);
    }

    @Transactional
    public UserProfileResponse updateProfile(UserAccount currentUser, UpdateProfileRequest request) {
        UserAccount user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new BusinessException(404, "用户不存在", HttpStatus.NOT_FOUND));

        String username = request.username() == null ? user.getUsername() : request.username().trim();
        if (username.isBlank()) {
            throw new BusinessException(400, "用户名不能为空");
        }
        if (!username.equals(user.getUsername()) && userRepository.existsByUsernameAndIdNot(username, user.getId())) {
            throw new BusinessException(409, "用户名已存在", HttpStatus.CONFLICT);
        }

        String avatarUrl = request.avatarUrl() == null ? user.getAvatarUrl() : request.avatarUrl().trim();
        user.updateProfile(username, avatarUrl);
        return toProfile(user);
    }

    @Transactional
    public UserProfileResponse recharge(UserAccount currentUser, RechargeRequest request) {
        UserAccount user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new BusinessException(404, "用户不存在", HttpStatus.NOT_FOUND));

        BigDecimal amount = request.amount();
        user.recharge(amount);
        return toProfile(user);
    }

    @Transactional
    public UserProfileResponse uploadAvatar(UserAccount currentUser, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "请选择头像文件");
        }
        if (file.getSize() > MAX_AVATAR_SIZE) {
            throw new BusinessException(400, "头像不能超过 2MB");
        }

        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED_AVATAR_TYPES.contains(contentType)) {
            throw new BusinessException(400, "头像仅支持 JPG、PNG、GIF、WEBP 格式");
        }

        UserAccount user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new BusinessException(404, "用户不存在", HttpStatus.NOT_FOUND));

        Path uploadPath = Path.of(avatarDir).toAbsolutePath().normalize();
        String filename = "user-" + user.getId() + "-" + UUID.randomUUID() + extensionFor(contentType);
        Path target = uploadPath.resolve(filename).normalize();
        if (!target.startsWith(uploadPath)) {
            throw new BusinessException(400, "头像文件名不合法");
        }

        try {
            Files.createDirectories(uploadPath);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new BusinessException(500, "头像上传失败", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        user.updateAvatar("/api/uploads/avatars/" + filename);
        return toProfile(user);
    }

    public UserProfileResponse toProfile(UserAccount user) {
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getAvatarUrl(),
                user.getBalance()
        );
    }

    private Optional<UserAccount> findByAccount(String account) {
        String normalized = account.toLowerCase(Locale.ROOT);
        return userRepository.findByEmail(normalized)
                .or(() -> userRepository.findByPhone(account))
                .or(() -> userRepository.findByUsername(account));
    }

    private AuthResponse createSessionResponse(UserAccount user) {
        String token = tokenHasher.generateToken();
        sessionRepository.save(new AuthSession(
                user,
                tokenHasher.hash(token),
                LocalDateTime.now().plusDays(SESSION_DAYS)
        ));
        return new AuthResponse(token, toProfile(user));
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
}
