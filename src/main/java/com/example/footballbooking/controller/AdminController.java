package com.example.footballbooking.controller;

import com.example.footballbooking.dto.ApiResponse;
import com.example.footballbooking.dto.PageResponse;
import com.example.footballbooking.dto.admin.StatisticsResponse;
import com.example.footballbooking.dto.admin.UpdateFieldStatusRequest;
import com.example.footballbooking.dto.admin.UpdateUserStatusRequest;
import com.example.footballbooking.dto.booking.BookingResponse;
import com.example.footballbooking.dto.field.FieldResponse;
import com.example.footballbooking.dto.user.UserResponse;
import com.example.footballbooking.service.AdminService;
import com.example.footballbooking.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API quản trị hệ thống (chỉ ADMIN, được chặn ở SecurityConfig).
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Quản trị hệ thống và thống kê")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;
    private final StatisticsService statisticsService;

    public AdminController(AdminService adminService, StatisticsService statisticsService) {
        this.adminService = adminService;
        this.statisticsService = statisticsService;
    }

    @GetMapping("/users")
    @Operation(summary = "Danh sách người dùng (USER)")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> users(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getUsers(PageRequest.of(page, size))));
    }

    @GetMapping("/owners")
    @Operation(summary = "Danh sách chủ sân (OWNER)")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> owners(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getOwners(PageRequest.of(page, size))));
    }

    @GetMapping("/fields")
    @Operation(summary = "Danh sách toàn bộ sân")
    public ResponseEntity<ApiResponse<PageResponse<FieldResponse>>> fields(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getFields(PageRequest.of(page, size))));
    }

    @GetMapping("/bookings")
    @Operation(summary = "Danh sách toàn bộ lượt đặt")
    public ResponseEntity<ApiResponse<PageResponse<BookingResponse>>> bookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getBookings(PageRequest.of(page, size))));
    }

    @PutMapping("/users/{id}/status")
    @Operation(summary = "Cập nhật trạng thái người dùng")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable Long id, @Valid @RequestBody UpdateUserStatusRequest request) {
        UserResponse user = adminService.updateUserStatus(id, request.status());
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái người dùng thành công", user));
    }

    @PutMapping("/fields/{id}/status")
    @Operation(summary = "Cập nhật trạng thái sân")
    public ResponseEntity<ApiResponse<FieldResponse>> updateFieldStatus(
            @PathVariable Long id, @Valid @RequestBody UpdateFieldStatusRequest request) {
        FieldResponse field = adminService.updateFieldStatus(id, request.status());
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái sân thành công", field));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Thống kê tổng quan hệ thống")
    public ResponseEntity<ApiResponse<StatisticsResponse>> statistics() {
        return ResponseEntity.ok(ApiResponse.success(statisticsService.getStatistics()));
    }
}
