package com.example.footballbooking.enums;

/**
 * Vòng đời trạng thái của một lượt đặt sân.
 *
 * <pre>
 * PENDING ──confirm──▶ CONFIRMED ──(hết giờ)──▶ COMPLETED
 *    │  │
 *    │  └──reject────▶ REJECTED
 *    └──cancel───────▶ CANCELLED
 * </pre>
 */
public enum BookingStatus {
    /** Vừa tạo, chờ chủ sân xác nhận. */
    PENDING,
    /** Chủ sân đã xác nhận. */
    CONFIRMED,
    /** Người đặt tự hủy. */
    CANCELLED,
    /** Chủ sân từ chối. */
    REJECTED,
    /** Đã diễn ra xong. */
    COMPLETED
}
