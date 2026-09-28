package com.example.footballbooking.service.impl;

import com.example.footballbooking.dto.PageResponse;
import com.example.footballbooking.dto.booking.BookingResponse;
import com.example.footballbooking.dto.field.FieldResponse;
import com.example.footballbooking.dto.user.UserResponse;
import com.example.footballbooking.entity.Field;
import com.example.footballbooking.entity.User;
import com.example.footballbooking.enums.ActiveStatus;
import com.example.footballbooking.enums.Role;
import com.example.footballbooking.enums.UserStatus;
import com.example.footballbooking.exception.ResourceNotFoundException;
import com.example.footballbooking.mapper.BookingMapper;
import com.example.footballbooking.mapper.FieldMapper;
import com.example.footballbooking.mapper.UserMapper;
import com.example.footballbooking.repository.BookingRepository;
import com.example.footballbooking.repository.FieldRepository;
import com.example.footballbooking.repository.UserRepository;
import com.example.footballbooking.service.AdminService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hiện thực nghiệp vụ quản trị hệ thống.
 */
@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final FieldRepository fieldRepository;
    private final BookingRepository bookingRepository;

    public AdminServiceImpl(UserRepository userRepository,
                            FieldRepository fieldRepository,
                            BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.fieldRepository = fieldRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getUsers(Pageable pageable) {
        return PageResponse.from(userRepository.findByRole(Role.USER, pageable), UserMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getOwners(Pageable pageable) {
        return PageResponse.from(userRepository.findByRole(Role.OWNER, pageable), UserMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FieldResponse> getFields(Pageable pageable) {
        return PageResponse.from(fieldRepository.findAll(pageable), FieldMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> getBookings(Pageable pageable) {
        return PageResponse.from(bookingRepository.findAllByOrderByCreatedAtDesc(pageable), BookingMapper::toResponse);
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        user.setStatus(status);
        return UserMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public FieldResponse updateFieldStatus(Long fieldId, ActiveStatus status) {
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sân bóng"));
        field.setStatus(status);
        return FieldMapper.toResponse(fieldRepository.save(field));
    }
}
