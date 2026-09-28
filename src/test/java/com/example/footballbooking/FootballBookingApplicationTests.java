package com.example.footballbooking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test khởi động (smoke test) xác nhận toàn bộ Spring Context nạp thành công.
 *
 * <p>Kích hoạt profile "test" để dùng H2 in-memory, đảm bảo test không phụ thuộc
 * vào MySQL đang chạy trên máy.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
class FootballBookingApplicationTests {

    @Test
    @DisplayName("Spring Context khởi động thành công")
    void contextLoads() {
        // Nếu context lỗi cấu hình, test sẽ tự động thất bại tại bước khởi động
    }
}
