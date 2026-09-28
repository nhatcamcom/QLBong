package com.example.footballbooking.repository;

import com.example.footballbooking.entity.Field;
import com.example.footballbooking.enums.ActiveStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Xây dựng điều kiện truy vấn động cho tìm kiếm sân.
 *
 * <p>Chỉ áp dụng những tiêu chí thực sự được truyền vào, giúp gộp nhiều bộ lọc
 * (từ khóa, địa chỉ, loại sân, khoảng giá) trong một truy vấn duy nhất.</p>
 */
public final class FieldSpecifications {

    private FieldSpecifications() {
    }

    public static Specification<Field> search(String keyword, String address, Long fieldTypeId,
                                              BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            // Tìm kiếm công khai chỉ hiển thị sân đang hoạt động
            predicates.add(cb.equal(root.get("status"), ActiveStatus.ACTIVE));

            if (StringUtils.hasText(keyword)) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("address")), pattern)));
            }
            if (StringUtils.hasText(address)) {
                predicates.add(cb.like(cb.lower(root.get("address")), "%" + address.trim().toLowerCase() + "%"));
            }
            if (fieldTypeId != null) {
                predicates.add(cb.equal(root.get("fieldType").get("id"), fieldTypeId));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("pricePerHour"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("pricePerHour"), maxPrice));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
