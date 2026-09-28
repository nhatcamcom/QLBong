package com.example.footballbooking.repository;

import com.example.footballbooking.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Truy cập dữ liệu thanh toán.
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /** Lấy thanh toán gắn với một booking. */
    Optional<Payment> findByBookingId(Long bookingId);
}
