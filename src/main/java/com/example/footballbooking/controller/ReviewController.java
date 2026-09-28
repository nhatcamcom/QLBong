package com.example.footballbooking.controller;

import com.example.footballbooking.dto.ApiResponse;
import com.example.footballbooking.dto.PageResponse;
import com.example.footballbooking.dto.review.CreateReviewRequest;
import com.example.footballbooking.dto.review.ReviewResponse;
import com.example.footballbooking.security.SecurityUtils;
import com.example.footballbooking.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API đánh giá sân.
 */
@RestController
@Tag(name = "Review", description = "Đánh giá sân")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/api/reviews")
    @Operation(summary = "Tạo đánh giá (chỉ người có lượt đặt hợp lệ)")
    public ResponseEntity<ApiResponse<ReviewResponse>> create(@Valid @RequestBody CreateReviewRequest request) {
        ReviewResponse created = reviewService.create(SecurityUtils.getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Đánh giá thành công", created));
    }

    @GetMapping("/api/fields/{id}/reviews")
    @Operation(summary = "Danh sách đánh giá của một sân")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> fieldReviews(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<ReviewResponse> result = reviewService.getFieldReviews(id, pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
