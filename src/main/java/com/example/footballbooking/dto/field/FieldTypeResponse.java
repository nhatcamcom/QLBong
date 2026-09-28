package com.example.footballbooking.dto.field;

import com.example.footballbooking.enums.ActiveStatus;

/**
 * Thông tin loại sân trả về cho Client.
 */
public record FieldTypeResponse(
        Long id,
        String name,
        String description,
        ActiveStatus status
) {
}
