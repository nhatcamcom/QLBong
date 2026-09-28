package com.example.footballbooking.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * Dữ liệu đăng nhập.
 */
public record LoginRequest(

        @NotBlank(message = "Email không được để trống")
        String email,

        @NotBlank(message = "Mật khẩu không được để trống")
        String password
) {
}
