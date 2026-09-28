package com.example.footballbooking.service;

import com.example.footballbooking.dto.PageResponse;
import com.example.footballbooking.dto.field.CreateFieldRequest;
import com.example.footballbooking.dto.field.FieldResponse;
import com.example.footballbooking.dto.field.UpdateFieldRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

/**
 * Nghiệp vụ quản lý sân bóng.
 *
 * <p>Các thao tác sửa/xóa đều kiểm tra quyền sở hữu tài nguyên: chỉ chính chủ sân
 * mới được thao tác trên sân của mình (chống IDOR / Broken Access Control).</p>
 */
public interface FieldService {

    /** Tìm kiếm sân công khai (chỉ sân ACTIVE), có lọc và phân trang. */
    PageResponse<FieldResponse> search(String keyword, String address, Long fieldTypeId,
                                       BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /** Xem chi tiết một sân, kèm điểm đánh giá trung bình. */
    FieldResponse getById(Long fieldId);

    /** Tạo sân mới; chủ sân là người đang đăng nhập. */
    FieldResponse create(Long ownerId, CreateFieldRequest request);

    /** Cập nhật sân; chỉ chủ sân của chính sân đó mới được sửa. */
    FieldResponse update(Long ownerId, Long fieldId, UpdateFieldRequest request);

    /** Xóa sân; chỉ chủ sân của chính sân đó mới được xóa. */
    void delete(Long ownerId, Long fieldId);

    /** Danh sách sân của một chủ sân (mọi trạng thái), có phân trang. */
    PageResponse<FieldResponse> getByOwner(Long ownerId, Pageable pageable);
}
