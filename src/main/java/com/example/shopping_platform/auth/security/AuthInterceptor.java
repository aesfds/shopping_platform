package com.example.shopping_platform.auth.security;

import com.example.shopping_platform.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        String token = extractToken(request);
        return authService.authenticate(token)
                .map(user -> {
                    request.setAttribute(CurrentUser.USER_ATTRIBUTE, user);
                    request.setAttribute(CurrentUser.TOKEN_ATTRIBUTE, token);
                    return true;
                })
                .orElseGet(() -> {
                    writeUnauthorized(response);
                    return false;
                });
    }

    public static String extractToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7).trim();
        }

        String headerToken = request.getHeader("X-Auth-Token");
        if (headerToken != null && !headerToken.isBlank()) {
            return headerToken.trim();
        }

        return null;
    }

    private void writeUnauthorized(HttpServletResponse response) {
        try {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"请先登录\",\"data\":null}");
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to write unauthorized response", exception);
        }
    }
}
