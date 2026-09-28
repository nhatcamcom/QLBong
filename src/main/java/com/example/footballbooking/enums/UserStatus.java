package com.example.footballbooking.enums;

/**
 * Trạng thái tài khoản người dùng.
 */
public enum UserStatus {
    /** Đang hoạt động, đăng nhập bình thường. */
    ACTIVE,
    /** Tạm ngưng (chưa kích hoạt hoặc tự vô hiệu hóa). */
    INACTIVE,
    /** Bị khóa bởi quản trị viên, không được đăng nhập. */
    BLOCKED
}
