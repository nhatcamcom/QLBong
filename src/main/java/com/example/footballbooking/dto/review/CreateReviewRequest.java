package com.example.footballbooking.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Dữ liệu tạo đánh giá. Sân được suy ra từ booking, không nhận trực tiếp fieldId
 * để đảm bảo đánh giá đúng sân đã đặt.
 */
public record CreateReviewRequest(

        @NotNull(message = "Thiếu thông tin lượt đặt cần đánh giá")
        Long bookingId,

        @NotNull(message = "Vui lòng chấm điểm")
        @Min(value = 1, message = "Điểm đánh giá tối thiểu là 1")
        @Max(value = 5, message = "Điểm đánh giá tối đa là 5")
        Integer rating,

        @Size(max = 1000, message = "Nội dung đánh giá tối đa 1000 ký tự")
        String comment
) {
}
