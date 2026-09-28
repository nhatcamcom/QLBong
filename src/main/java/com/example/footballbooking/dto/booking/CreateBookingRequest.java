package com.example.footballbooking.dto.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Dữ liệu tạo booking.
 *
 * <p>Chú ý: KHÔNG có trường {@code totalPrice}. Giá do Backend tự tính từ
 * giá thuê của sân và thời lượng, tuyệt đối không nhận giá từ Client.</p>
 */
public record CreateBookingRequest(

        @NotNull(message = "Vui lòng chọn sân")
        Long fieldId,

        @NotNull(message = "Vui lòng chọn ngày đặt")
        @FutureOrPresent(message = "Ngày đặt không được ở quá khứ")
        LocalDate bookingDate,

        @NotNull(message = "Vui lòng chọn giờ bắt đầu")
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,

        @NotNull(message = "Vui lòng chọn giờ kết thúc")
        @JsonFormat(pattern = "HH:mm")
        LocalTime endTime
) {
}
