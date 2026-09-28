package com.example.footballbooking.security;

import com.example.footballbooking.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Tiện ích lấy thông tin người dùng đang đăng nhập từ SecurityContext.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /** Lấy {@link CustomUserDetails} của người dùng hiện tại, ném 401 nếu chưa đăng nhập. */
    public static CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new UnauthorizedException("Yêu cầu đăng nhập để thực hiện thao tác này");
        }
        return userDetails;
    }

    /** Lấy id người dùng hiện tại. */
    public static Long getCurrentUserId() {
        return getCurrentUserDetails().getId();
    }

    /** Kiểm tra người dùng hiện tại có phải ADMIN hay không. */
    public static boolean isCurrentUserAdmin() {
        return getCurrentUserDetails().getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}
