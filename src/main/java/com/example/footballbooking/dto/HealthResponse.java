package com.example.footballbooking.dto;

/**
 * DTO mô tả trạng thái sức khỏe (health) của dịch vụ.
 *
 * <p>Đây là ví dụ minh họa luồng cơ bản của bài thực hành đầu tiên:
 * HTTP Request → Controller → Java Object → JSON Response.</p>
 *
 * @param status  trạng thái dịch vụ, ví dụ "UP"
 * @param service tên dịch vụ đang chạy
 */
public record HealthResponse(String status, String service) {
}
