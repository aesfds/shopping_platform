package com.example.shopping_platform.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "请输入用户名")
        @Size(min = 2, max = 20, message = "用户名长度为 2 到 20 位")
        String username,

        @NotBlank(message = "请输入邮箱")
        @Email(message = "邮箱格式不正确")
        String email,

        @NotBlank(message = "请输入手机号")
        @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
        String phone,

        @NotBlank(message = "请输入密码")
        @Size(min = 6, max = 60, message = "密码长度为 6 到 60 位")
        String password,

        @NotBlank(message = "请再次输入密码")
        String confirmPassword
) {
}
