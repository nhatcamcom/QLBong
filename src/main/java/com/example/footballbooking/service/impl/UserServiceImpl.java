package com.example.footballbooking.service.impl;

import com.example.footballbooking.dto.user.UpdateUserRequest;
import com.example.footballbooking.dto.user.UserResponse;
import com.example.footballbooking.entity.User;
import com.example.footballbooking.exception.ResourceNotFoundException;
import com.example.footballbooking.mapper.UserMapper;
import com.example.footballbooking.repository.UserRepository;
import com.example.footballbooking.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hiện thực nghiệp vụ hồ sơ người dùng.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getProfile(Long userId) {
        return UserMapper.toResponse(findUserOrThrow(userId));
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateUserRequest request) {
        User user = findUserOrThrow(userId);
        // Chỉ cho phép cập nhật họ tên và số điện thoại; email/role/status không đổi ở đây
        user.setFullName(request.fullName());
        user.setPhone(request.phone());
        return UserMapper.toResponse(userRepository.save(user));
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }
}
