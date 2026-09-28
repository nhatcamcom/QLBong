package com.example.footballbooking.service;

import com.example.footballbooking.dto.field.FieldResponse;
import com.example.footballbooking.dto.field.UpdateFieldRequest;
import com.example.footballbooking.entity.Field;
import com.example.footballbooking.entity.FieldType;
import com.example.footballbooking.entity.User;
import com.example.footballbooking.enums.ActiveStatus;
import com.example.footballbooking.exception.ConflictException;
import com.example.footballbooking.exception.ForbiddenException;
import com.example.footballbooking.repository.BookingRepository;
import com.example.footballbooking.repository.FieldRepository;
import com.example.footballbooking.repository.FieldTypeRepository;
import com.example.footballbooking.repository.ReviewRepository;
import com.example.footballbooking.repository.UserRepository;
import com.example.footballbooking.service.impl.FieldServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test cho nghiệp vụ quản lý sân, trọng tâm là kiểm tra quyền sở hữu tài nguyên.
 */
@ExtendWith(MockitoExtension.class)
class FieldServiceImplTest {

    @Mock
    private FieldRepository fieldRepository;
    @Mock
    private FieldTypeRepository fieldTypeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private FieldServiceImpl fieldService;

    private Field fieldOwnedBy(Long ownerId) {
        User owner = new User();
        owner.setId(ownerId);
        owner.setFullName("Chủ sân");
        FieldType type = new FieldType();
        type.setId(1L);
        type.setName("Sân 7 người");
        type.setStatus(ActiveStatus.ACTIVE);
        Field field = new Field();
        field.setId(1L);
        field.setOwner(owner);
        field.setFieldType(type);
        field.setName("Sân A");
        field.setAddress("Hà Nội");
        field.setPricePerHour(new BigDecimal("300000"));
        field.setStatus(ActiveStatus.ACTIVE);
        return field;
    }

    private UpdateFieldRequest updateRequest() {
        return new UpdateFieldRequest(1L, "Sân A", "Hà Nội", "mô tả",
                new BigDecimal("350000"), null, ActiveStatus.ACTIVE);
    }

    @Test
    @DisplayName("Owner khác sửa sân không phải của mình → 403 FORBIDDEN")
    void update_byNonOwner_throwsForbidden() {
        Field field = fieldOwnedBy(1L); // sân của owner id = 1
        when(fieldRepository.findById(1L)).thenReturn(Optional.of(field));

        Long otherOwnerId = 2L;
        assertThatThrownBy(() -> fieldService.update(otherOwnerId, 1L, updateRequest()))
                .isInstanceOf(ForbiddenException.class);
        verify(fieldRepository, never()).save(any());
    }

    @Test
    @DisplayName("Chính chủ cập nhật sân → thành công")
    void update_byOwner_success() {
        Field field = fieldOwnedBy(1L);
        when(fieldRepository.findById(1L)).thenReturn(Optional.of(field));
        when(fieldTypeRepository.findById(1L)).thenReturn(Optional.of(field.getFieldType()));
        when(fieldRepository.save(any(Field.class))).thenAnswer(inv -> inv.getArgument(0));

        FieldResponse response = fieldService.update(1L, 1L, updateRequest());

        assertThat(response.pricePerHour()).isEqualByComparingTo("350000");
    }

    @Test
    @DisplayName("Xóa sân đã có lượt đặt → 409 CONFLICT")
    void delete_fieldWithBookings_throwsConflict() {
        Field field = fieldOwnedBy(1L);
        when(fieldRepository.findById(1L)).thenReturn(Optional.of(field));
        when(bookingRepository.existsByFieldId(1L)).thenReturn(true);

        assertThatThrownBy(() -> fieldService.delete(1L, 1L))
                .isInstanceOf(ConflictException.class);
        verify(fieldRepository, never()).delete(any(Field.class));
    }
}
