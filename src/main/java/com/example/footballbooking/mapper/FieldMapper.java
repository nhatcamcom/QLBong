package com.example.footballbooking.mapper;

import com.example.footballbooking.dto.field.FieldResponse;
import com.example.footballbooking.dto.field.FieldTypeResponse;
import com.example.footballbooking.entity.Field;
import com.example.footballbooking.entity.FieldType;

/**
 * Chuyển đổi entity sân/loại sân sang DTO.
 *
 * <p>Việc truy cập {@code owner}/{@code fieldType} (LAZY) phải nằm trong một
 * transaction đang mở. Ở API danh sách, để tránh N+1 khi map nhiều sân,
 * dựa vào cấu hình {@code hibernate.default_batch_fetch_size} (nạp theo lô).</p>
 */
public final class FieldMapper {

    private FieldMapper() {
    }

    public static FieldTypeResponse toTypeResponse(FieldType type) {
        return new FieldTypeResponse(type.getId(), type.getName(), type.getDescription(), type.getStatus());
    }

    /** Dùng cho API danh sách: không kèm điểm đánh giá để tránh truy vấn thừa. */
    public static FieldResponse toResponse(Field field) {
        return toResponse(field, null, null);
    }

    /** Dùng cho API chi tiết: kèm điểm trung bình và số lượng đánh giá. */
    public static FieldResponse toResponse(Field field, Double averageRating, Long reviewCount) {
        return new FieldResponse(
                field.getId(),
                field.getName(),
                field.getAddress(),
                field.getDescription(),
                field.getPricePerHour(),
                field.getImageUrl(),
                field.getStatus(),
                toTypeResponse(field.getFieldType()),
                new FieldResponse.OwnerSummary(field.getOwner().getId(), field.getOwner().getFullName()),
                averageRating,
                reviewCount
        );
    }
}
