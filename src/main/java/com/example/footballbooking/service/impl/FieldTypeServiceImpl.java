package com.example.footballbooking.service.impl;

import com.example.footballbooking.dto.field.FieldTypeResponse;
import com.example.footballbooking.mapper.FieldMapper;
import com.example.footballbooking.repository.FieldTypeRepository;
import com.example.footballbooking.service.FieldTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Hiện thực nghiệp vụ loại sân.
 */
@Service
public class FieldTypeServiceImpl implements FieldTypeService {

    private final FieldTypeRepository fieldTypeRepository;

    public FieldTypeServiceImpl(FieldTypeRepository fieldTypeRepository) {
        this.fieldTypeRepository = fieldTypeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FieldTypeResponse> getAll() {
        return fieldTypeRepository.findAll().stream()
                .map(FieldMapper::toTypeResponse)
                .toList();
    }
}
