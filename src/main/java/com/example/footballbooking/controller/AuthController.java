package com.example.footballbooking.controller;

import com.example.footballbooking.dto.ApiResponse;
import com.example.footballbooking.dto.auth.LoginRequest;
import com.example.footballbooking.dto.auth.LoginResponse;
import com.example.footballbooking.dto.auth.RegisterRequest;
import com.example.footballbooking.dto.user.UserResponse;
import com.example.footballbooking.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API xác thực: đăng ký và đăng nhập.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Đăng ký và đăng nhập")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản mới")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Đăng ký thành công", user));
    }

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập, trả về JWT")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", response));
    }
}
