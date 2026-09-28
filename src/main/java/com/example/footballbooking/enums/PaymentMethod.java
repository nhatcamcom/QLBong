package com.example.footballbooking.enums;

/**
 * Hình thức thanh toán. Mặc định tiền mặt tại sân do chưa tích hợp cổng thanh toán.
 */
public enum PaymentMethod {
    /** Tiền mặt tại sân. */
    CASH,
    /** Chuyển khoản ngân hàng. */
    BANK_TRANSFER
}
