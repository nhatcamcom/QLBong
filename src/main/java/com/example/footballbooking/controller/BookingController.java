package com.example.footballbooking.controller;

import com.example.footballbooking.dto.ApiResponse;
import com.example.footballbooking.dto.PageResponse;
import com.example.footballbooking.dto.booking.BookingResponse;
import com.example.footballbooking.dto.booking.CreateBookingRequest;
import com.example.footballbooking.security.SecurityUtils;
import com.example.footballbooking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API đặt sân cho người dùng.
 */
@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking", description = "Đặt sân và quản lý lượt đặt của tôi")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @Operation(summary = "Tạo lượt đặt sân")
    public ResponseEntity<ApiResponse<BookingResponse>> create(@Valid @RequestBody CreateBookingRequest request) {
        BookingResponse created = bookingService.create(SecurityUtils.getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Đặt sân thành công", created));
    }

    @GetMapping("/my")
    @Operation(summary = "Danh sách lượt đặt của tôi")
    public ResponseEntity<ApiResponse<PageResponse<BookingResponse>>> myBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<BookingResponse> result = bookingService.getMyBookings(SecurityUtils.getCurrentUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xem chi tiết lượt đặt")
    public ResponseEntity<ApiResponse<BookingResponse>> getById(@PathVariable Long id) {
        BookingResponse booking = bookingService.getById(
                SecurityUtils.getCurrentUserId(), SecurityUtils.isCurrentUserAdmin(), id);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Hủy lượt đặt của tôi")
    public ResponseEntity<ApiResponse<BookingResponse>> cancel(@PathVariable Long id) {
        BookingResponse booking = bookingService.cancel(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.ok(ApiResponse.success("Đã hủy lượt đặt", booking));
    }
}
