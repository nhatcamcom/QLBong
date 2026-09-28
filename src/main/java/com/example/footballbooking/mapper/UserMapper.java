package com.example.footballbooking.mapper;

import com.example.footballbooking.dto.user.UserResponse;
import com.example.footballbooking.entity.User;

/**
 * Chuyển đổi entity {@link User} sang DTO trả về, đảm bảo không lộ mật khẩu ra ngoài.
 */
public final class UserMapper {

    private UserMapper() {
        // Lớp tiện ích, không cho tạo instance
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }
}
