package com.example.footballbooking.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Dữ liệu cập nhật hồ sơ cá nhân. Không cho phép đổi email/role/status qua API này.
 */
public record UpdateUserRequest(

        @NotBlank(message = "Họ tên không được để trống")
        String fullName,

        @Pattern(regexp = "^(0\\d{9})?$", message = "Số điện thoại phải gồm 10 chữ số bắt đầu bằng 0")
        String phone
) {
}
