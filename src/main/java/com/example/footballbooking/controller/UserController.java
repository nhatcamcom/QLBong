package com.example.footballbooking.controller;

import com.example.footballbooking.dto.ApiResponse;
import com.example.footballbooking.dto.user.UpdateUserRequest;
import com.example.footballbooking.dto.user.UserResponse;
import com.example.footballbooking.security.SecurityUtils;
import com.example.footballbooking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API hồ sơ người dùng hiện tại.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "Hồ sơ cá nhân")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Xem hồ sơ của tôi")
    public ResponseEntity<ApiResponse<UserResponse>> getMe() {
        UserResponse user = userService.getProfile(SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PutMapping("/me")
    @Operation(summary = "Cập nhật hồ sơ của tôi")
    public ResponseEntity<ApiResponse<UserResponse>> updateMe(@Valid @RequestBody UpdateUserRequest request) {
        UserResponse user = userService.updateProfile(SecurityUtils.getCurrentUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật hồ sơ thành công", user));
    }
}
