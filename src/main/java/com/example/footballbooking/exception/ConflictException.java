package com.example.footballbooking.exception;

/**
 * Ném ra khi thao tác gây xung đột với trạng thái hiện tại của hệ thống.
 *
 * <p>Ví dụ: email đã tồn tại khi đăng ký, hoặc khung giờ sân đã có người đặt.
 * Được {@code GlobalExceptionHandler} ánh xạ sang HTTP 409 CONFLICT.</p>
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
