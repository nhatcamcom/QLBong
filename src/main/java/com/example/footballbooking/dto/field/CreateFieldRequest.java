package com.example.footballbooking.dto.field;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Dữ liệu tạo sân mới. Chủ sở hữu (owner) được lấy từ người đang đăng nhập,
 * không nhận từ Client để tránh giả mạo.
 */
public record CreateFieldRequest(

        @NotNull(message = "Loại sân không được để trống")
        Long fieldTypeId,

        @NotBlank(message = "Tên sân không được để trống")
        String name,

        @NotBlank(message = "Địa chỉ sân không được để trống")
        String address,

        String description,

        @NotNull(message = "Giá thuê theo giờ không được để trống")
        @DecimalMin(value = "0.0", inclusive = false, message = "Giá thuê phải lớn hơn 0")
        BigDecimal pricePerHour,

        String imageUrl
) {
}
