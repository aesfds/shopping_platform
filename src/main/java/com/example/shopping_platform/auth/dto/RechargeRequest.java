package com.example.shopping_platform.auth.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RechargeRequest(
        @NotNull(message = "请输入充值金额")
        @DecimalMin(value = "0.01", message = "充值金额必须大于 0")
        @Digits(integer = 8, fraction = 2, message = "充值金额最多 8 位整数和 2 位小数")
        BigDecimal amount
) {
}
