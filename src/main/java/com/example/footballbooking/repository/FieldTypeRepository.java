package com.example.footballbooking.repository;

import com.example.footballbooking.entity.FieldType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Truy cập dữ liệu loại sân.
 */
public interface FieldTypeRepository extends JpaRepository<FieldType, Long> {

    /** Tìm loại sân theo tên (dùng khi khởi tạo dữ liệu mẫu, tránh tạo trùng). */
    Optional<FieldType> findByName(String name);
}
