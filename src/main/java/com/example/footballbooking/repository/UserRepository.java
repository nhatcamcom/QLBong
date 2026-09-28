package com.example.footballbooking.repository;

import com.example.footballbooking.entity.User;
import com.example.footballbooking.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Truy cập dữ liệu người dùng.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /** Tìm người dùng theo email (dùng khi đăng nhập/xác thực). */
    Optional<User> findByEmail(String email);

    /** Kiểm tra email đã tồn tại chưa (dùng khi đăng ký). */
    boolean existsByEmail(String email);

    /** Liệt kê người dùng theo vai trò, có phân trang (dùng cho Admin). */
    Page<User> findByRole(Role role, Pageable pageable);

    /** Đếm số người dùng theo vai trò (dùng cho thống kê). */
    long countByRole(Role role);
}
