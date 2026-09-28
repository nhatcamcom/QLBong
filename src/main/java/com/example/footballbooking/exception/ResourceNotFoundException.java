package com.example.footballbooking.exception;

/**
 * Ném ra khi không tìm thấy tài nguyên yêu cầu (sân, người dùng, booking...).
 *
 * <p>Được {@code GlobalExceptionHandler} ánh xạ sang HTTP 404 NOT FOUND.</p>
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
