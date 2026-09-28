package com.example.footballbooking.service;

import com.example.footballbooking.dto.admin.StatisticsResponse;

/**
 * Nghiệp vụ thống kê tổng quan cho Admin.
 */
public interface StatisticsService {

    StatisticsResponse getStatistics();
}
