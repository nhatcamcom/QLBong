package com.example.footballbooking.security;

import com.example.footballbooking.entity.User;
import com.example.footballbooking.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Bọc entity {@link User} để Spring Security sử dụng cho xác thực/phân quyền.
 *
 * <p>Giữ thêm {@code id} để tầng nghiệp vụ lấy nhanh id người dùng hiện tại
 * mà không cần truy vấn lại database.</p>
 */
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final UserStatus status;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.status = user.getStatus();
        // Spring Security quy ước quyền theo vai trò có tiền tố "ROLE_"
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    public Long getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Tài khoản bị khóa không được đăng nhập
        return status != UserStatus.BLOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Chỉ tài khoản đang hoạt động mới được xác thực thành công
        return status == UserStatus.ACTIVE;
    }
}
