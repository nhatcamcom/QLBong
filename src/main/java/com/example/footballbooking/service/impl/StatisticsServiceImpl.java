package com.example.footballbooking.service.impl;

import com.example.footballbooking.dto.admin.StatisticsResponse;
import com.example.footballbooking.enums.BookingStatus;
import com.example.footballbooking.enums.Role;
import com.example.footballbooking.repository.BookingRepository;
import com.example.footballbooking.repository.FieldRepository;
import com.example.footballbooking.repository.UserRepository;
import com.example.footballbooking.service.StatisticsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Hiện thực nghiệp vụ thống kê.
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    // Các trạng thái được tính vào doanh thu
    private static final List<BookingStatus> REVENUE_STATUSES =
            List.of(BookingStatus.CONFIRMED, BookingStatus.COMPLETED);

    private final UserRepository userRepository;
    private final FieldRepository fieldRepository;
    private final BookingRepository bookingRepository;

    public StatisticsServiceImpl(UserRepository userRepository,
                                 FieldRepository fieldRepository,
                                 BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.fieldRepository = fieldRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public StatisticsResponse getStatistics() {
        // Đếm số booking theo từng trạng thái
        Map<BookingStatus, Long> bookingsByStatus = new EnumMap<>(BookingStatus.class);
        for (BookingStatus status : BookingStatus.values()) {
            bookingsByStatus.put(status, bookingRepository.countByStatus(status));
        }

        BigDecimal totalRevenue = bookingRepository.sumTotalPriceByStatuses(REVENUE_STATUSES);

        return new StatisticsResponse(
                userRepository.countByRole(Role.USER),
                userRepository.countByRole(Role.OWNER),
                fieldRepository.count(),
                bookingRepository.count(),
                bookingsByStatus,
                totalRevenue
        );
    }
}
