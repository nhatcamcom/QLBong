package com.example.footballbooking.controller;

import com.example.footballbooking.dto.ApiResponse;
import com.example.footballbooking.dto.PageResponse;
import com.example.footballbooking.dto.booking.BookingResponse;
import com.example.footballbooking.security.SecurityUtils;
import com.example.footballbooking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API cho chủ sân (OWNER) xử lý các lượt đặt trên sân của mình.
 */
@RestController
@RequestMapping("/api/owner/bookings")
@Tag(name = "Owner - Booking", description = "Chủ sân xác nhận/từ chối lượt đặt")
@SecurityRequirement(name = "bearerAuth")
public class OwnerBookingController {

    private final BookingService bookingService;

    public OwnerBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    @Operation(summary = "Danh sách lượt đặt trên các sân của tôi")
    public ResponseEntity<ApiResponse<PageResponse<BookingResponse>>> ownerBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<BookingResponse> result = bookingService.getOwnerBookings(SecurityUtils.getCurrentUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Xác nhận lượt đặt")
    public ResponseEntity<ApiResponse<BookingResponse>> confirm(@PathVariable Long id) {
        BookingResponse booking = bookingService.confirm(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.ok(ApiResponse.success("Đã xác nhận lượt đặt", booking));
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Từ chối lượt đặt")
    public ResponseEntity<ApiResponse<BookingResponse>> reject(@PathVariable Long id) {
        BookingResponse booking = bookingService.reject(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.ok(ApiResponse.success("Đã từ chối lượt đặt", booking));
    }
}
