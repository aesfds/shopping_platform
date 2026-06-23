package com.example.shopping_platform.auth.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(min = 2, max = 40, message = "用户名长度为 2-40 个字符")
        String username,

        @Size(max = 255, message = "头像地址不能超过 255 个字符")
        String avatarUrl
) {
}
