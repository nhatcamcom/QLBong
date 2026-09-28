package com.example.footballbooking.service.impl;

import com.example.footballbooking.dto.PageResponse;
import com.example.footballbooking.dto.field.CreateFieldRequest;
import com.example.footballbooking.dto.field.FieldResponse;
import com.example.footballbooking.dto.field.UpdateFieldRequest;
import com.example.footballbooking.entity.Field;
import com.example.footballbooking.entity.FieldType;
import com.example.footballbooking.entity.User;
import com.example.footballbooking.enums.ActiveStatus;
import com.example.footballbooking.exception.BadRequestException;
import com.example.footballbooking.exception.ConflictException;
import com.example.footballbooking.exception.ForbiddenException;
import com.example.footballbooking.exception.ResourceNotFoundException;
import com.example.footballbooking.mapper.FieldMapper;
import com.example.footballbooking.repository.BookingRepository;
import com.example.footballbooking.repository.FieldRepository;
import com.example.footballbooking.repository.FieldSpecifications;
import com.example.footballbooking.repository.FieldTypeRepository;
import com.example.footballbooking.repository.ReviewRepository;
import com.example.footballbooking.repository.UserRepository;
import com.example.footballbooking.service.FieldService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Hiện thực nghiệp vụ quản lý sân.
 */
@Service
public class FieldServiceImpl implements FieldService {

    private final FieldRepository fieldRepository;
    private final FieldTypeRepository fieldTypeRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    public FieldServiceImpl(FieldRepository fieldRepository,
                            FieldTypeRepository fieldTypeRepository,
                            UserRepository userRepository,
                            ReviewRepository reviewRepository,
                            BookingRepository bookingRepository) {
        this.fieldRepository = fieldRepository;
        this.fieldTypeRepository = fieldTypeRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FieldResponse> search(String keyword, String address, Long fieldTypeId,
                                              BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Specification<Field> spec = FieldSpecifications.search(keyword, address, fieldTypeId, minPrice, maxPrice);
        Page<Field> page = fieldRepository.findAll(spec, pageable);
        return PageResponse.from(page, FieldMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public FieldResponse getById(Long fieldId) {
        Field field = findFieldOrThrow(fieldId);
        Double averageRating = reviewRepository.findAverageRatingByFieldId(fieldId);
        long reviewCount = reviewRepository.countByFieldId(fieldId);
        return FieldMapper.toResponse(field, averageRating, reviewCount);
    }

    @Override
    @Transactional
    public FieldResponse create(Long ownerId, CreateFieldRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chủ sân"));
        FieldType fieldType = findFieldTypeOrThrow(request.fieldTypeId());

        Field field = new Field();
        field.setOwner(owner);
        field.setFieldType(fieldType);
        field.setName(request.name());
        field.setAddress(request.address());
        field.setDescription(request.description());
        field.setPricePerHour(request.pricePerHour());
        field.setImageUrl(request.imageUrl());
        field.setStatus(ActiveStatus.ACTIVE);

        return FieldMapper.toResponse(fieldRepository.save(field));
    }

    @Override
    @Transactional
    public FieldResponse update(Long ownerId, Long fieldId, UpdateFieldRequest request) {
        Field field = findFieldOrThrow(fieldId);
        verifyOwnership(field, ownerId);
        FieldType fieldType = findFieldTypeOrThrow(request.fieldTypeId());

        field.setFieldType(fieldType);
        field.setName(request.name());
        field.setAddress(request.address());
        field.setDescription(request.description());
        field.setPricePerHour(request.pricePerHour());
        field.setImageUrl(request.imageUrl());
        field.setStatus(request.status());

        return FieldMapper.toResponse(fieldRepository.save(field));
    }

    @Override
    @Transactional
    public void delete(Long ownerId, Long fieldId) {
        Field field = findFieldOrThrow(fieldId);
        verifyOwnership(field, ownerId);
        // Sân đã có lượt đặt thì không xóa để giữ toàn vẹn dữ liệu lịch sử; hãy chuyển INACTIVE
        if (bookingRepository.existsByFieldId(fieldId)) {
            throw new ConflictException("Không thể xóa sân đã có lượt đặt. Vui lòng chuyển sân sang trạng thái INACTIVE");
        }
        fieldRepository.delete(field);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FieldResponse> getByOwner(Long ownerId, Pageable pageable) {
        Page<Field> page = fieldRepository.findByOwnerId(ownerId, pageable);
        return PageResponse.from(page, FieldMapper::toResponse);
    }

    private Field findFieldOrThrow(Long fieldId) {
        return fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sân bóng"));
    }

    private FieldType findFieldTypeOrThrow(Long fieldTypeId) {
        return fieldTypeRepository.findById(fieldTypeId)
                .orElseThrow(() -> new BadRequestException("Loại sân không tồn tại"));
    }

    /**
     * Kiểm tra quyền sở hữu: chỉ chính chủ sân mới được thao tác.
     * Đây là kiểm tra ở cấp tài nguyên, không chỉ dựa vào vai trò ROLE_OWNER.
     */
    private void verifyOwnership(Field field, Long ownerId) {
        if (!field.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Bạn không phải chủ sở hữu của sân này");
        }
    }
}
