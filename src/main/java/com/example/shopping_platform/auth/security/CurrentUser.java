package com.example.shopping_platform.auth.security;

import com.example.shopping_platform.auth.entity.UserAccount;
import com.example.shopping_platform.common.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

public final class CurrentUser {

    public static final String USER_ATTRIBUTE = "currentUser";
    public static final String TOKEN_ATTRIBUTE = "currentToken";

    private CurrentUser() {
    }

    public static UserAccount require(HttpServletRequest request) {
        Object value = request.getAttribute(USER_ATTRIBUTE);
        if (value instanceof UserAccount user) {
            return user;
        }
        throw new BusinessException(401, "请先登录", HttpStatus.UNAUTHORIZED);
    }

    public static String requireToken(HttpServletRequest request) {
        Object value = request.getAttribute(TOKEN_ATTRIBUTE);
        if (value instanceof String token) {
            return token;
        }
        throw new BusinessException(401, "请先登录", HttpStatus.UNAUTHORIZED);
    }
}
