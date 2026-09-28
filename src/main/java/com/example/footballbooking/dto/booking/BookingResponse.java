package com.example.footballbooking.dto.booking;

import com.example.footballbooking.enums.BookingStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Thông tin booking trả về cho Client.
 */
public record BookingResponse(
        Long id,
        Long fieldId,
        String fieldName,
        Long userId,
        String userFullName,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate bookingDate,

        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,

        @JsonFormat(pattern = "HH:mm")
        LocalTime endTime,

        BigDecimal totalPrice,
        BookingStatus status,
        LocalDateTime createdAt
) {
}
