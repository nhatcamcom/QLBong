package com.example.footballbooking.service;

import com.example.footballbooking.dto.field.FieldTypeResponse;

import java.util.List;

/**
 * Nghiệp vụ liên quan loại sân (phục vụ Frontend hiển thị danh sách chọn).
 */
public interface FieldTypeService {

    /** Lấy toàn bộ loại sân. */
    List<FieldTypeResponse> getAll();
}
