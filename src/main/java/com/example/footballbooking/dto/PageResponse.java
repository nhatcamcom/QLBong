package com.example.footballbooking.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * Cấu trúc dữ liệu phân trang trả về cho Frontend.
 *
 * @param content       danh sách phần tử của trang hiện tại
 * @param page          chỉ số trang hiện tại (bắt đầu từ 0)
 * @param size          kích thước trang
 * @param totalElements tổng số phần tử
 * @param totalPages    tổng số trang
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    /**
     * Chuyển một {@link Page} entity thành {@link PageResponse} DTO,
     * áp dụng hàm {@code mapper} cho từng phần tử để không lộ Entity ra ngoài.
     */
    public static <E, D> PageResponse<D> from(Page<E> page, Function<E, D> mapper) {
        List<D> items = page.getContent().stream().map(mapper).toList();
        return new PageResponse<>(items, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }
}
