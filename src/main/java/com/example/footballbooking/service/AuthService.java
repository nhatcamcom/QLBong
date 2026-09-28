package com.example.footballbooking.service;

import com.example.footballbooking.dto.auth.LoginRequest;
import com.example.footballbooking.dto.auth.LoginResponse;
import com.example.footballbooking.dto.auth.RegisterRequest;
import com.example.footballbooking.dto.user.UserResponse;

/**
 * Nghiệp vụ xác thực: đăng ký và đăng nhập.
 */
public interface AuthService {

    /** Đăng ký tài khoản mới (vai trò USER). */
    UserResponse register(RegisterRequest request);

    /** Đăng nhập, trả về JWT nếu thông tin hợp lệ. */
    LoginResponse login(LoginRequest request);
}
