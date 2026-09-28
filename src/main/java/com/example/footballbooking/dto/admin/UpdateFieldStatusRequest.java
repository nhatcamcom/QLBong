package com.example.footballbooking.dto.admin;

import com.example.footballbooking.enums.ActiveStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Dữ liệu Admin cập nhật trạng thái sân.
 */
public record UpdateFieldStatusRequest(

        @NotNull(message = "Trạng thái không được để trống")
        ActiveStatus status
) {
}
