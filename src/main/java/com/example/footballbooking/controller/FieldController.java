package com.example.footballbooking.controller;

import com.example.footballbooking.dto.ApiResponse;
import com.example.footballbooking.dto.PageResponse;
import com.example.footballbooking.dto.field.CreateFieldRequest;
import com.example.footballbooking.dto.field.FieldResponse;
import com.example.footballbooking.dto.field.UpdateFieldRequest;
import com.example.footballbooking.security.SecurityUtils;
import com.example.footballbooking.service.FieldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * REST API sân bóng (xem công khai; tạo/sửa/xóa cần vai trò OWNER hoặc ADMIN).
 */
@RestController
@RequestMapping("/api/fields")
@Tag(name = "Field", description = "Quản lý và tra cứu sân bóng")
public class FieldController {

    private final FieldService fieldService;

    public FieldController(FieldService fieldService) {
        this.fieldService = fieldService;
    }

    @GetMapping
    @Operation(summary = "Tìm kiếm sân (từ khóa, địa chỉ, loại sân, khoảng giá) + phân trang")
    public ResponseEntity<ApiResponse<PageResponse<FieldResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Long fieldType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<FieldResponse> result =
                fieldService.search(keyword, address, fieldType, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xem chi tiết sân")
    public ResponseEntity<ApiResponse<FieldResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(fieldService.getById(id)));
    }

    @PostMapping
    @Operation(summary = "Tạo sân mới (OWNER/ADMIN)")
    public ResponseEntity<ApiResponse<FieldResponse>> create(@Valid @RequestBody CreateFieldRequest request) {
        FieldResponse created = fieldService.create(SecurityUtils.getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Tạo sân thành công", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật sân (chỉ chủ sân)")
    public ResponseEntity<ApiResponse<FieldResponse>> update(@PathVariable Long id,
                                                             @Valid @RequestBody UpdateFieldRequest request) {
        FieldResponse updated = fieldService.update(SecurityUtils.getCurrentUserId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật sân thành công", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa sân (chỉ chủ sân)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        fieldService.delete(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa sân", null));
    }
}
