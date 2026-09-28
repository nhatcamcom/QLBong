package com.example.footballbooking.service;

import com.example.footballbooking.dto.user.UpdateUserRequest;
import com.example.footballbooking.dto.user.UserResponse;

/**
 * Nghiệp vụ quản lý hồ sơ người dùng hiện tại.
 */
public interface UserService {

    /** Lấy thông tin hồ sơ của người dùng đang đăng nhập. */
    UserResponse getProfile(Long userId);

    /** Cập nhật hồ sơ của người dùng đang đăng nhập. */
    UserResponse updateProfile(Long userId, UpdateUserRequest request);
}
