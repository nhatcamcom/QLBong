package com.example.footballbooking.dto.user;

import com.example.footballbooking.enums.Role;
import com.example.footballbooking.enums.UserStatus;

import java.time.LocalDateTime;

/**
 * Thông tin người dùng trả về cho Client (không bao giờ chứa mật khẩu).
 */
public record UserResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        Role role,
        UserStatus status,
        LocalDateTime createdAt
) {
}
