package com.example.footballbooking.controller;

import com.example.footballbooking.dto.ApiResponse;
import com.example.footballbooking.dto.field.FieldTypeResponse;
import com.example.footballbooking.service.FieldTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API loại sân (công khai, phục vụ Frontend hiển thị danh sách chọn).
 */
@RestController
@RequestMapping("/api/field-types")
@Tag(name = "FieldType", description = "Danh mục loại sân")
public class FieldTypeController {

    private final FieldTypeService fieldTypeService;

    public FieldTypeController(FieldTypeService fieldTypeService) {
        this.fieldTypeService = fieldTypeService;
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách loại sân")
    public ResponseEntity<ApiResponse<List<FieldTypeResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(fieldTypeService.getAll()));
    }
}
