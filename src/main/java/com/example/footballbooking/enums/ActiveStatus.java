package com.example.footballbooking.enums;

/**
 * Trạng thái đóng/mở dùng chung cho Sân (Field) và Loại sân (FieldType).
 * Chỉ sân đang {@link #ACTIVE} mới cho phép đặt.
 */
public enum ActiveStatus {
    ACTIVE,
    INACTIVE
}
