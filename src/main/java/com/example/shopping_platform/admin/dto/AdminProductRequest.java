package com.example.shopping_platform.admin.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record AdminProductRequest(
        @NotBlank(message = "请输入商品名称")
        @Size(max = 100, message = "商品名称不能超过 100 个字符")
        String name,

        @NotBlank(message = "请输入商品副标题")
        @Size(max = 160, message = "商品副标题不能超过 160 个字符")
        String subtitle,

        @Size(max = 800, message = "商品说明不能超过 800 个字符")
        String description,

        @NotNull(message = "请输入商品价格")
        @DecimalMin(value = "0.01", message = "商品价格必须大于 0")
        BigDecimal price,

        BigDecimal originalPrice,

        Long sales,

        @Size(max = 32, message = "标签不能超过 32 个字符")
        String tag,

        @Size(max = 255, message = "图片地址不能超过 255 个字符")
        String imageUrl,

        Integer stock,

        List<String> specs,

        @NotNull(message = "请选择商品分类")
        Long categoryId,

        Integer sortOrder,

        Boolean available
) {
}
