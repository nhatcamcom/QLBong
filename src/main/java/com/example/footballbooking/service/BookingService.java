package com.example.footballbooking.service;

import com.example.footballbooking.dto.PageResponse;
import com.example.footballbooking.dto.booking.BookingResponse;
import com.example.footballbooking.dto.booking.CreateBookingRequest;
import org.springframework.data.domain.Pageable;

/**
 * Nghiệp vụ đặt sân — module quan trọng nhất.
 *
 * <p>Đảm bảo: tự tính giá ở Backend, kiểm tra trùng lịch và chống đặt trùng
 * (double booking) trong điều kiện chạy song song.</p>
 */
public interface BookingService {

    /** Tạo booking mới cho người dùng hiện tại. */
    BookingResponse create(Long userId, CreateBookingRequest request);

    /** Xem chi tiết một booking (chủ booking, chủ sân, hoặc admin). */
    BookingResponse getById(Long currentUserId, boolean isAdmin, Long bookingId);

    /** Danh sách booking của người dùng hiện tại. */
    PageResponse<BookingResponse> getMyBookings(Long userId, Pageable pageable);

    /** Người dùng tự hủy booking của mình. */
    BookingResponse cancel(Long userId, Long bookingId);

    /** Chủ sân xác nhận booking. */
    BookingResponse confirm(Long ownerId, Long bookingId);

    /** Chủ sân từ chối booking. */
    BookingResponse reject(Long ownerId, Long bookingId);

    /** Danh sách booking thuộc các sân của chủ sân hiện tại. */
    PageResponse<BookingResponse> getOwnerBookings(Long ownerId, Pageable pageable);
}
