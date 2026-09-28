package com.example.footballbooking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình tài liệu OpenAPI/Swagger cho Backend.
 *
 * <p>Định nghĩa sẵn security scheme kiểu Bearer JWT để khi triển khai
 * xác thực ở các Phase sau, nút "Authorize" trên Swagger UI đã sẵn sàng
 * cho phép dán token dạng {@code Bearer <JWT>}.</p>
 */
@Configuration
public class OpenApiConfig {

    /** Tên security scheme dùng chung, tham chiếu ở các API cần xác thực. */
    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI footballBookingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Football Booking API")
                        .description("REST API cho Hệ thống quản lý và đặt sân bóng")
                        .version("v1")
                        .contact(new Contact().name("Nhóm 4 - SOA"))
                        .license(new License().name("Educational Use")))
                // Khai báo scheme Bearer JWT để tái sử dụng cho các endpoint bảo mật
                .schemaRequirement(BEARER_SCHEME, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Dán Access Token nhận được sau khi đăng nhập"));
    }
}
