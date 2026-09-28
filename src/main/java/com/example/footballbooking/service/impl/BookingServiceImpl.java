package com.example.footballbooking.service.impl;

import com.example.footballbooking.dto.PageResponse;
import com.example.footballbooking.dto.booking.BookingResponse;
import com.example.footballbooking.dto.booking.CreateBookingRequest;
import com.example.footballbooking.entity.Booking;
import com.example.footballbooking.entity.Field;
import com.example.footballbooking.entity.Payment;
import com.example.footballbooking.entity.User;
import com.example.footballbooking.enums.ActiveStatus;
import com.example.footballbooking.enums.BookingStatus;
import com.example.footballbooking.enums.PaymentMethod;
import com.example.footballbooking.enums.PaymentStatus;
import com.example.footballbooking.exception.BadRequestException;
import com.example.footballbooking.exception.ConflictException;
import com.example.footballbooking.exception.ForbiddenException;
import com.example.footballbooking.exception.ResourceNotFoundException;
import com.example.footballbooking.mapper.BookingMapper;
import com.example.footballbooking.repository.BookingRepository;
import com.example.footballbooking.repository.FieldRepository;
import com.example.footballbooking.repository.PaymentRepository;
import com.example.footballbooking.repository.UserRepository;
import com.example.footballbooking.service.BookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Hiện thực nghiệp vụ đặt sân.
 */
@Service
public class BookingServiceImpl implements BookingService {

    // Các trạng thái đang thực sự giữ chỗ khung giờ (dùng để kiểm tra trùng lịch)
    private static final List<BookingStatus> ACTIVE_BOOKING_STATUSES =
            List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

    private static final int MINUTES_PER_HOUR = 60;

    private final BookingRepository bookingRepository;
    private final FieldRepository fieldRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              FieldRepository fieldRepository,
                              UserRepository userRepository,
                              PaymentRepository paymentRepository) {
        this.bookingRepository = bookingRepository;
        this.fieldRepository = fieldRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public BookingResponse create(Long userId, CreateBookingRequest request) {
        // (1) Kiểm tra tính hợp lệ của thời gian trước khi chạm database
        validateTime(request);

        // (2) Khóa dòng sân (PESSIMISTIC_WRITE) để tuần tự hóa các yêu cầu đặt cùng một sân.
        //     Nhờ đó bước "kiểm tra trùng lịch → lưu" là nguyên tử trước tình huống chạy song song,
        //     ngăn chặn double booking (hai người đặt trùng khung giờ cùng lúc).
        Field field = fieldRepository.findByIdForUpdate(request.fieldId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sân bóng"));

        // (3) Chỉ nhận đặt sân đang hoạt động
        if (field.getStatus() != ActiveStatus.ACTIVE) {
            throw new BadRequestException("Sân hiện không nhận đặt (không ở trạng thái ACTIVE)");
        }

        // (4) Kiểm tra trùng khung giờ trên cùng sân, cùng ngày
        boolean overlapped = bookingRepository.existsOverlappingBooking(
                field.getId(), request.bookingDate(), request.startTime(), request.endTime(),
                ACTIVE_BOOKING_STATUSES);
        if (overlapped) {
            throw new ConflictException("Khung giờ này đã có người đặt, vui lòng chọn khung giờ khác");
        }

        // (5) Backend tự tính tổng tiền, tuyệt đối không tin giá do Frontend gửi
        BigDecimal totalPrice = calculateTotalPrice(field.getPricePerHour(),
                request.startTime(), request.endTime());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        // (6) Tạo và lưu booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setField(field);
        booking.setBookingDate(request.bookingDate());
        booking.setStartTime(request.startTime());
        booking.setEndTime(request.endTime());
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.PENDING);
        Booking saved = bookingRepository.save(booking);

        // (7) Tạo bản ghi thanh toán ở mức cơ bản (chờ thanh toán, mặc định tiền mặt)
        Payment payment = new Payment();
        payment.setBooking(saved);
        payment.setAmount(totalPrice);
        payment.setPaymentMethod(PaymentMethod.CASH);
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        return BookingMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getById(Long currentUserId, boolean isAdmin, Long bookingId) {
        Booking booking = findBookingOrThrow(bookingId);
        boolean isBookingOwner = booking.getUser().getId().equals(currentUserId);
        boolean isFieldOwner = booking.getField().getOwner().getId().equals(currentUserId);
        // Chỉ chủ booking, chủ sân của booking đó, hoặc admin mới được xem
        if (!isAdmin && !isBookingOwner && !isFieldOwner) {
            throw new ForbiddenException("Bạn không có quyền xem lượt đặt này");
        }
        return BookingMapper.toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> getMyBookings(Long userId, Pageable pageable) {
        Page<Booking> page = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PageResponse.from(page, BookingMapper::toResponse);
    }

    @Override
    @Transactional
    public BookingResponse cancel(Long userId, Long bookingId) {
        Booking booking = findBookingOrThrow(bookingId);
        if (!booking.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Bạn chỉ có thể hủy lượt đặt của chính mình");
        }
        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new ConflictException("Không thể hủy lượt đặt ở trạng thái hiện tại");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        // Nếu đã thanh toán thì đánh dấu hoàn tiền
        paymentRepository.findByBookingId(bookingId).ifPresent(payment -> {
            if (payment.getStatus() == PaymentStatus.PAID) {
                payment.setStatus(PaymentStatus.REFUNDED);
            }
        });
        return BookingMapper.toResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse confirm(Long ownerId, Long bookingId) {
        Booking booking = findBookingOrThrow(bookingId);
        verifyFieldOwner(booking, ownerId);
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ConflictException("Chỉ có thể xác nhận lượt đặt đang chờ (PENDING)");
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        paymentRepository.findByBookingId(bookingId)
                .ifPresent(payment -> payment.setStatus(PaymentStatus.PAID));
        return BookingMapper.toResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse reject(Long ownerId, Long bookingId) {
        Booking booking = findBookingOrThrow(bookingId);
        verifyFieldOwner(booking, ownerId);
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ConflictException("Chỉ có thể từ chối lượt đặt đang chờ (PENDING)");
        }
        booking.setStatus(BookingStatus.REJECTED);
        return BookingMapper.toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookingResponse> getOwnerBookings(Long ownerId, Pageable pageable) {
        Page<Booking> page = bookingRepository.findByFieldOwnerIdOrderByCreatedAtDesc(ownerId, pageable);
        return PageResponse.from(page, BookingMapper::toResponse);
    }

    // ============================ Hàm hỗ trợ nội bộ ============================

    private void validateTime(CreateBookingRequest request) {
        if (request.bookingDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Ngày đặt không được ở quá khứ");
        }
        if (!request.startTime().isBefore(request.endTime())) {
            throw new BadRequestException("Giờ bắt đầu phải trước giờ kết thúc");
        }
        // Nếu đặt trong ngày hôm nay, giờ bắt đầu phải ở tương lai
        if (request.bookingDate().isEqual(LocalDate.now())
                && !request.startTime().isAfter(LocalTime.now())) {
            throw new BadRequestException("Giờ bắt đầu phải ở thời điểm trong tương lai");
        }
    }

    /**
     * Tính tổng tiền = giá theo giờ × số giờ thuê.
     * Ví dụ: 300.000đ/giờ, đặt 18:00–20:00 → 600.000đ.
     */
    private BigDecimal calculateTotalPrice(BigDecimal pricePerHour, LocalTime start, LocalTime end) {
        long minutes = Duration.between(start, end).toMinutes();
        BigDecimal hours = BigDecimal.valueOf(minutes)
                .divide(BigDecimal.valueOf(MINUTES_PER_HOUR), 4, RoundingMode.HALF_UP);
        return pricePerHour.multiply(hours).setScale(0, RoundingMode.HALF_UP);
    }

    private Booking findBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lượt đặt"));
    }

    private void verifyFieldOwner(Booking booking, Long ownerId) {
        if (!booking.getField().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Bạn không phải chủ sân của lượt đặt này");
        }
    }
}
