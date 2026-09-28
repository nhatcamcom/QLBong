package com.example.footballbooking.dto.field;

import com.example.footballbooking.enums.ActiveStatus;

import java.math.BigDecimal;

/**
 * Thông tin sân trả về cho Client.
 *
 * <p>{@code averageRating} và {@code reviewCount} chỉ được điền ở API xem chi tiết sân;
 * ở API danh sách sẽ để null nhằm tránh truy vấn N+1 khi lấy nhiều sân.</p>
 */
public record FieldResponse(
        Long id,
        String name,
        String address,
        String description,
        BigDecimal pricePerHour,
        String imageUrl,
        ActiveStatus status,
        FieldTypeResponse fieldType,
        OwnerSummary owner,
        Double averageRating,
        Long reviewCount
) {

    /** Thông tin chủ sân rút gọn. */
    public record OwnerSummary(Long id, String fullName) {
    }
}
