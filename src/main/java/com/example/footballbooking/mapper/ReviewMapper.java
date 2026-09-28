package com.example.footballbooking.mapper;

import com.example.footballbooking.dto.review.ReviewResponse;
import com.example.footballbooking.entity.Review;

/**
 * Chuyển đổi entity {@link Review} sang DTO.
 */
public final class ReviewMapper {

    private ReviewMapper() {
    }

    public static ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getField().getId(),
                review.getUser().getId(),
                review.getUser().getFullName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}
