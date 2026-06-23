package com.example.shopping_platform.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "请输入手机号、邮箱或用户名")
        String account,

        @NotBlank(message = "请输入密码")
        @Size(min = 6, max = 60, message = "密码长度为 6 到 60 位")
        String password
) {
}
