package com.example.footballbooking.exception;

/**
 * Ném ra khi yêu cầu cần xác thực nhưng người dùng chưa đăng nhập.
 *
 * <p>Được {@code GlobalExceptionHandler} ánh xạ sang HTTP 401 UNAUTHORIZED.</p>
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
