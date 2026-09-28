package com.example.footballbooking.dto.admin;

import com.example.footballbooking.enums.UserStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Dữ liệu Admin cập nhật trạng thái tài khoản người dùng.
 */
public record UpdateUserStatusRequest(

        @NotNull(message = "Trạng thái không được để trống")
        UserStatus status
) {
}
