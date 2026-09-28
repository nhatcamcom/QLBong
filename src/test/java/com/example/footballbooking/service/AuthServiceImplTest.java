package com.example.footballbooking.service;

import com.example.footballbooking.dto.auth.LoginRequest;
import com.example.footballbooking.dto.auth.LoginResponse;
import com.example.footballbooking.dto.auth.RegisterRequest;
import com.example.footballbooking.entity.User;
import com.example.footballbooking.enums.Role;
import com.example.footballbooking.enums.UserStatus;
import com.example.footballbooking.exception.ConflictException;
import com.example.footballbooking.exception.UnauthorizedException;
import com.example.footballbooking.repository.UserRepository;
import com.example.footballbooking.security.JwtService;
import com.example.footballbooking.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test cho nghiệp vụ xác thực (đăng ký/đăng nhập).
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("Đăng ký trùng email → 409 CONFLICT")
    void register_duplicateEmail_throwsConflict() {
        when(userRepository.existsByEmail("a@gmail.com")).thenReturn(true);
        RegisterRequest request = new RegisterRequest("A", "a@gmail.com", "123456", "0901234567");
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("Đăng ký thành công: mật khẩu được mã hóa, vai trò USER, trạng thái ACTIVE")
    void register_success_encodesPasswordAndDefaults() {
        when(userRepository.existsByEmail("a@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("HASHED");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        authService.register(new RegisterRequest("A", "a@gmail.com", "123456", "0901234567"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getPassword()).isEqualTo("HASHED");        // không lưu plaintext
        assertThat(saved.getRole()).isEqualTo(Role.USER);
        assertThat(saved.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("Đăng nhập thành công → trả JWT dạng Bearer")
    void login_success_returnsToken() {
        User user = new User();
        user.setId(1L);
        user.setFullName("A");
        user.setEmail("a@gmail.com");
        user.setRole(Role.USER);
        when(userRepository.findByEmail("a@gmail.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("a@gmail.com", "USER")).thenReturn("jwt-token");

        LoginResponse response = authService.login(new LoginRequest("a@gmail.com", "123456"));

        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.user().role()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("Sai thông tin đăng nhập → 401 UNAUTHORIZED")
    void login_badCredentials_throwsUnauthorized() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad"));
        assertThatThrownBy(() -> authService.login(new LoginRequest("a@gmail.com", "wrong")))
                .isInstanceOf(UnauthorizedException.class);
    }
}
