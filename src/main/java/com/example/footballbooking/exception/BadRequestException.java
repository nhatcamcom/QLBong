package com.example.footballbooking.exception;

/**
 * Ném ra khi dữ liệu/yêu cầu không hợp lệ về mặt nghiệp vụ.
 *
 * <p>Ví dụ: giờ bắt đầu lớn hơn giờ kết thúc, đặt sân trong quá khứ...
 * Được {@code GlobalExceptionHandler} ánh xạ sang HTTP 400 BAD REQUEST.</p>
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
