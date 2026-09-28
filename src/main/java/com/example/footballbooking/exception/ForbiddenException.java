package com.example.footballbooking.exception;

/**
 * Ném ra khi người dùng đã đăng nhập nhưng không đủ quyền trên tài nguyên cụ thể.
 *
 * <p>Dùng cho kiểm tra quyền sở hữu tài nguyên (ví dụ: Owner B sửa sân của Owner A).
 * Được {@code GlobalExceptionHandler} ánh xạ sang HTTP 403 FORBIDDEN.</p>
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
