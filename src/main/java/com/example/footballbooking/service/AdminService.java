package com.example.footballbooking.service;

import com.example.footballbooking.dto.PageResponse;
import com.example.footballbooking.dto.booking.BookingResponse;
import com.example.footballbooking.dto.field.FieldResponse;
import com.example.footballbooking.dto.user.UserResponse;
import com.example.footballbooking.enums.ActiveStatus;
import com.example.footballbooking.enums.UserStatus;
import org.springframework.data.domain.Pageable;

/**
 * Nghiệp vụ quản trị hệ thống (chỉ ADMIN).
 */
public interface AdminService {

    /** Danh sách tài khoản người dùng (vai trò USER). */
    PageResponse<UserResponse> getUsers(Pageable pageable);

    /** Danh sách tài khoản chủ sân (vai trò OWNER). */
    PageResponse<UserResponse> getOwners(Pageable pageable);

    /** Danh sách toàn bộ sân (mọi trạng thái). */
    PageResponse<FieldResponse> getFields(Pageable pageable);

    /** Danh sách toàn bộ lượt đặt. */
    PageResponse<BookingResponse> getBookings(Pageable pageable);

    /** Cập nhật trạng thái tài khoản người dùng. */
    UserResponse updateUserStatus(Long userId, UserStatus status);

    /** Cập nhật trạng thái sân. */
    FieldResponse updateFieldStatus(Long fieldId, ActiveStatus status);
}
