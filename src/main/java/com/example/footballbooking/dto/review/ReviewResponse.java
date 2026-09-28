package com.example.footballbooking.dto.review;

import java.time.LocalDateTime;

/**
 * Thông tin đánh giá trả về cho Client.
 */
public record ReviewResponse(
        Long id,
        Long fieldId,
        Long userId,
        String userFullName,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {
}
