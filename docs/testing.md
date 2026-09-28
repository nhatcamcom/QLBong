# Kiểm thử (Testing)

## Công cụ
JUnit 5, Mockito, Spring Boot Test (MockMvc). Test dùng **H2 in-memory** (profile `test`)
nên chạy độc lập, không cần MySQL thật.

## Cách chạy
```bash
mvn test
```

## Phân loại
- **Unit test**: kiểm tra Service bằng Mockito (mock Repository). Ưu tiên: UserService,
  FieldService, BookingService, ReviewService, AuthService.
- **Slice test** (`@WebMvcTest`): kiểm tra Controller không cần DB.
- **Integration test** (`@SpringBootTest`): kiểm tra luồng thật qua nhiều tầng, dùng H2.

## Test hiện có (23 test, PASS)
| Test | Loại | Nội dung |
|---|---|---|
| `HealthControllerTest` | `@SpringBootTest` | `GET /api/health` → 200, đúng JSON |
| `FootballBookingApplicationTests` | `@SpringBootTest` | Spring context nạp thành công trên H2 |
| `GlobalExceptionHandlerIntegrationTest` | `@SpringBootTest` | Đường dẫn lạ → 404 (không phải 500) |
| `BookingServiceImplTest` | Unit (Mockito) | Booking hợp lệ, sai giờ, field không tồn tại, field inactive, trùng lịch (409), tính tiền, hủy |
| `FieldServiceImplTest` | Unit (Mockito) | Quyền sở hữu (403), cập nhật, xóa sân đã có booking (409) |
| `AuthServiceImplTest` | Unit (Mockito) | Trùng email (409), mã hóa mật khẩu, đăng nhập trả JWT, sai mật khẩu (401) |
| `AuthApiIntegrationTest` | `@SpringBootTest` + MockMvc | Đăng ký→đăng nhập→xem hồ sơ, 401 khi thiếu token, 403 khi USER gọi ADMIN, 400 validation |

Chạy: `mvn test`. Tất cả dùng H2 in-memory (profile `test`), không cần MySQL.
