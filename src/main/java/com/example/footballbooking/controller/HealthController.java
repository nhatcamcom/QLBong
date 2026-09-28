package com.example.footballbooking.controller;

import com.example.footballbooking.dto.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller kiểm tra tình trạng hoạt động của dịch vụ.
 *
 * <p>Endpoint này không chứa nghiệp vụ nên trả về DTO trực tiếp, dùng để
 * Frontend hoặc hệ thống giám sát (monitoring) xác nhận Backend còn sống.</p>
 */
@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Kiểm tra tình trạng hoạt động của Backend")
public class HealthController {

    /**
     * Trả về trạng thái dịch vụ.
     *
     * <p>Không bọc trong {@code ApiResponse} vì đây là endpoint chẩn đoán,
     * cần giữ cấu trúc phẳng, ổn định cho công cụ giám sát đọc nhanh.</p>
     */
    @GetMapping
    @Operation(summary = "Trả về trạng thái UP nếu dịch vụ đang hoạt động")
    public ResponseEntity<HealthResponse> health() {
        HealthResponse response = new HealthResponse("UP", "Football Booking Service");
        return ResponseEntity.ok(response);
    }
}
