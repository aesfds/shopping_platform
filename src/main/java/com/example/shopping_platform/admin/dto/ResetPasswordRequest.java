package com.example.shopping_platform.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "请输入新密码")
        @Size(min = 6, max = 60, message = "密码长度为 6 到 60 位")
        String newPassword
) {
}
