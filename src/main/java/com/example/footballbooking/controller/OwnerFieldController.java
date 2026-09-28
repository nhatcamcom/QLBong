package com.example.footballbooking.controller;

import com.example.footballbooking.dto.ApiResponse;
import com.example.footballbooking.dto.PageResponse;
import com.example.footballbooking.dto.field.CreateFieldRequest;
import com.example.footballbooking.dto.field.FieldResponse;
import com.example.footballbooking.dto.field.UpdateFieldRequest;
import com.example.footballbooking.security.SecurityUtils;
import com.example.footballbooking.service.FieldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

/**
 * REST API cho chủ sân (OWNER) quản lý các sân của chính mình.
 *
 * <p>Mọi thao tác đều gắn với id người dùng đang đăng nhập; tầng Service kiểm tra
 * quyền sở hữu để chủ sân này không thao tác được lên sân của chủ sân khác.</p>
 */
@RestController
@RequestMapping("/api/owner/fields")
@Tag(name = "Owner - Field", description = "Chủ sân quản lý sân của mình")
@SecurityRequirement(name = "bearerAuth")
public class OwnerFieldController {

    private final FieldService fieldService;

    public OwnerFieldController(FieldService fieldService) {
        this.fieldService = fieldService;
    }

    @GetMapping
    @Operation(summary = "Danh sách sân của tôi")
    public ResponseEntity<ApiResponse<PageResponse<FieldResponse>>> myFields(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<FieldResponse> result = fieldService.getByOwner(SecurityUtils.getCurrentUserId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping
    @Operation(summary = "Tạo sân cho tôi")
    public ResponseEntity<ApiResponse<FieldResponse>> create(@Valid @RequestBody CreateFieldRequest request) {
        FieldResponse created = fieldService.create(SecurityUtils.getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Tạo sân thành công", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật sân của tôi")
    public ResponseEntity<ApiResponse<FieldResponse>> update(@PathVariable Long id,
                                                             @Valid @RequestBody UpdateFieldRequest request) {
        FieldResponse updated = fieldService.update(SecurityUtils.getCurrentUserId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật sân thành công", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa sân của tôi")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        fieldService.delete(SecurityUtils.getCurrentUserId(), id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa sân", null));
    }
}
