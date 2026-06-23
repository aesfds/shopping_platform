package com.example.shopping_platform.config;

import com.example.shopping_platform.admin.security.AdminInterceptor;
import com.example.shopping_platform.auth.security.AuthInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${shopping.cors.allowed-origins:http://localhost:5173}")
    private String[] allowedOrigins;

    @Value("${shopping.upload.avatar-dir:./uploads/avatars}")
    private String avatarDir;

    @Value("${shopping.upload.product-dir:./uploads/products}")
    private String productImageDir;

    private final AuthInterceptor authInterceptor;
    private final AdminInterceptor adminInterceptor;

    public WebConfig(AuthInterceptor authInterceptor, AdminInterceptor adminInterceptor) {
        this.authInterceptor = authInterceptor;
        this.adminInterceptor = adminInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String avatarLocation = Path.of(avatarDir).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/api/uploads/avatars/**")
                .addResourceLocations(avatarLocation);
        String productImageLocation = Path.of(productImageDir).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/api/uploads/products/**")
                .addResourceLocations(productImageLocation);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns(
                        "/api/auth/profile",
                        "/api/auth/avatar",
                        "/api/auth/recharge",
                        "/api/auth/logout",
                        "/api/cart/**",
                        "/api/orders/**"
                );
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/admin/**")
                .excludePathPatterns("/api/admin/login");
    }
}
