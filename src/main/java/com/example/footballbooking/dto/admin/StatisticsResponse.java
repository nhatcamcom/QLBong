package com.example.footballbooking.dto.admin;

import com.example.footballbooking.enums.BookingStatus;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Số liệu thống kê tổng quan cho Admin.
 *
 * @param totalUsers       tổng số tài khoản vai trò USER
 * @param totalOwners      tổng số tài khoản vai trò OWNER
 * @param totalFields      tổng số sân
 * @param totalBookings    tổng số lượt đặt
 * @param bookingsByStatus số lượng booking theo từng trạng thái
 * @param totalRevenue     doanh thu từ booking đã xác nhận/hoàn thành
 */
public record StatisticsResponse(
        long totalUsers,
        long totalOwners,
        long totalFields,
        long totalBookings,
        Map<BookingStatus, Long> bookingsByStatus,
        BigDecimal totalRevenue
) {
}
