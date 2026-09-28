package com.example.footballbooking.mapper;

import com.example.footballbooking.dto.booking.BookingResponse;
import com.example.footballbooking.entity.Booking;

/**
 * Chuyển đổi entity {@link Booking} sang DTO.
 */
public final class BookingMapper {

    private BookingMapper() {
    }

    public static BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getField().getId(),
                booking.getField().getName(),
                booking.getUser().getId(),
                booking.getUser().getFullName(),
                booking.getBookingDate(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getTotalPrice(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }
}
