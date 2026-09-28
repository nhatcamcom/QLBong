package com.example.footballbooking.dto.auth;

import com.example.footballbooking.enums.Role;

/**
 * Kết quả trả về sau khi đăng nhập thành công.
 *
 * @param accessToken JWT dùng cho các request tiếp theo
 * @param tokenType   loại token, luôn là "Bearer"
 * @param user        thông tin rút gọn của người dùng
 */
public record LoginResponse(
        String accessToken,
        String tokenType,
        UserSummary user
) {

    /** Thông tin người dùng rút gọn kèm trong phản hồi đăng nhập. */
    public record UserSummary(Long id, String fullName, Role role) {
    }
}
