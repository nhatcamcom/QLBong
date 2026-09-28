package com.example.footballbooking.enums;

/**
 * Vai trò người dùng trong hệ thống. Chỉ có đúng ba vai trò, không tạo thêm.
 */
public enum Role {
    /** Người dùng thường: đặt sân, đánh giá, quản lý booking của mình. */
    USER,
    /** Chủ sân: quản lý sân của mình, xác nhận/từ chối booking. */
    OWNER,
    /** Quản trị viên: quản lý toàn bộ hệ thống. */
    ADMIN
}
