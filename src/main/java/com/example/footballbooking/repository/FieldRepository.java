package com.example.footballbooking.repository;

import com.example.footballbooking.entity.Field;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Truy cập dữ liệu sân bóng.
 *
 * <p>Kế thừa {@link JpaSpecificationExecutor} để hỗ trợ tìm kiếm động
 * (theo từ khóa, địa chỉ, loại sân, khoảng giá) mà không cần viết nhiều truy vấn.</p>
 */
public interface FieldRepository extends JpaRepository<Field, Long>, JpaSpecificationExecutor<Field> {

    /** Liệt kê sân của một chủ sân, có phân trang. */
    Page<Field> findByOwnerId(Long ownerId, Pageable pageable);

    /**
     * Lấy sân kèm khóa ghi bi quan (PESSIMISTIC_WRITE).
     *
     * <p>Dùng trong quy trình tạo booking: khóa dòng sân để tuần tự hóa các yêu cầu
     * đặt cùng một sân, nhờ đó việc "kiểm tra trùng lịch rồi mới lưu" trở nên an toàn
     * trước tình huống chạy song song (race condition), tránh đặt trùng (double booking).</p>
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM Field f WHERE f.id = :id")
    Optional<Field> findByIdForUpdate(@Param("id") Long id);
}
