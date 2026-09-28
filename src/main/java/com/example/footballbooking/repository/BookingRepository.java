package com.example.footballbooking.repository;

import com.example.footballbooking.entity.Booking;
import com.example.footballbooking.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;

/**
 * Truy cập dữ liệu booking.
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /** Booking của một người dùng, phân trang, mới nhất trước. */
    Page<Booking> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /** Booking thuộc các sân của một chủ sân, phân trang, mới nhất trước. */
    Page<Booking> findByFieldOwnerIdOrderByCreatedAtDesc(Long ownerId, Pageable pageable);

    /** Tất cả booking (cho Admin), phân trang, mới nhất trước. */
    Page<Booking> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Kiểm tra có tồn tại booking trùng khung giờ trên cùng sân, cùng ngày hay không.
     *
     * <p>Hai khoảng [s1,e1) và [s2,e2) giao nhau khi {@code s1 < e2 AND s2 < e1}.
     * Chỉ xét các booking đang giữ chỗ (PENDING/CONFIRMED); booking đã hủy/từ chối
     * không chiếm khung giờ.</p>
     */
    @Query("""
            SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
            FROM Booking b
            WHERE b.field.id = :fieldId
              AND b.bookingDate = :bookingDate
              AND b.status IN :activeStatuses
              AND b.startTime < :endTime
              AND b.endTime > :startTime
            """)
    boolean existsOverlappingBooking(@Param("fieldId") Long fieldId,
                                     @Param("bookingDate") LocalDate bookingDate,
                                     @Param("startTime") LocalTime startTime,
                                     @Param("endTime") LocalTime endTime,
                                     @Param("activeStatuses") Collection<BookingStatus> activeStatuses);

    /** Sân đã từng có lượt đặt hay chưa (dùng để chặn xóa sân đang có booking). */
    boolean existsByFieldId(Long fieldId);

    /** Đếm booking theo trạng thái (dùng cho thống kê). */
    long countByStatus(BookingStatus status);

    /** Tổng doanh thu từ các booking ở những trạng thái tính tiền (dùng cho thống kê). */
    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b WHERE b.status IN :statuses")
    BigDecimal sumTotalPriceByStatuses(@Param("statuses") Collection<BookingStatus> statuses);
}
