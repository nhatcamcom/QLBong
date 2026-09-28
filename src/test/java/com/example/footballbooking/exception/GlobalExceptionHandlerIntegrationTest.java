package com.example.footballbooking.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test kiểm tra xử lý lỗi ở mức toàn ứng dụng (nạp cả GlobalExceptionHandler).
 *
 * <p>Mục tiêu: đảm bảo đường dẫn không tồn tại trả về đúng 404, KHÔNG bị bộ bắt lỗi
 * tổng quát biến thành 500.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GlobalExceptionHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Đường dẫn hợp lệ /api/health trả về 200")
    void validPath_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Đường dẫn không tồn tại trả về 404 (không phải 500)")
    @WithMockUser // Đã xác thực để vượt qua tầng bảo mật, kiểm tra đúng nhánh 404 của handler
    void unknownPath_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/khong-ton-tai"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
