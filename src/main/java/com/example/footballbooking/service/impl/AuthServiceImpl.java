package com.example.footballbooking.service.impl;

import com.example.footballbooking.dto.auth.LoginRequest;
import com.example.footballbooking.dto.auth.LoginResponse;
import com.example.footballbooking.dto.auth.RegisterRequest;
import com.example.footballbooking.dto.user.UserResponse;
import com.example.footballbooking.entity.User;
import com.example.footballbooking.enums.Role;
import com.example.footballbooking.enums.UserStatus;
import com.example.footballbooking.exception.ConflictException;
import com.example.footballbooking.exception.UnauthorizedException;
import com.example.footballbooking.mapper.UserMapper;
import com.example.footballbooking.repository.UserRepository;
import com.example.footballbooking.security.JwtService;
import com.example.footballbooking.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hiện thực nghiệp vụ xác thực.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final String TOKEN_TYPE = "Bearer";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // Email là định danh duy nhất: chặn đăng ký trùng để tránh xung đột dữ liệu
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email đã được sử dụng");
        }

        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        // Luôn mã hóa mật khẩu bằng BCrypt trước khi lưu, không lưu plaintext
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone());
        user.setRole(Role.USER);
        user.setStatus(UserStatus.ACTIVE);

        return UserMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        try {
            // Ủy quyền cho Spring Security kiểm tra email + mật khẩu (BCrypt) và trạng thái tài khoản
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (AuthenticationException ex) {
            // Không tiết lộ sai ở email hay mật khẩu để tránh dò tài khoản (user enumeration)
            throw new UnauthorizedException("Email hoặc mật khẩu không đúng");
        }

        // Xác thực thành công thì chắc chắn tồn tại người dùng
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("Email hoặc mật khẩu không đúng"));

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        LoginResponse.UserSummary summary =
                new LoginResponse.UserSummary(user.getId(), user.getFullName(), user.getRole());
        return new LoginResponse(token, TOKEN_TYPE, summary);
    }
}
