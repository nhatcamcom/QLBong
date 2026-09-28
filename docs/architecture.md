# Kiến trúc (Architecture)

## Tổng quan
Backend là **Modular Monolith**: một ứng dụng Spring Boot duy nhất, chia module theo package,
chưa tách Microservices (không dùng Eureka/Gateway/Kafka/Docker ở giai đoạn này).

## Luồng xử lý một request
```
Next.js (Frontend)
   │  HTTP + JSON
   ▼
Controller   → nhận request, validate đầu vào, KHÔNG chứa business logic
   ▼
Service      → toàn bộ business logic, transaction, kiểm tra nghiệp vụ
   ▼
Repository   → truy cập dữ liệu qua Spring Data JPA
   ▼
MySQL
```

## Nguyên tắc bắt buộc
- **Cấm** Controller gọi thẳng Repository. Luôn qua Service.
- **Cấm** trả Entity JPA trực tiếp ra ngoài. Luôn dùng `Entity → Mapper → DTO → JSON`.
- Business logic chỉ nằm trong Service.
- Mọi phản hồi bọc trong `ApiResponse<T>` (trừ endpoint chẩn đoán `/api/health`).
- Mọi ngoại lệ xử lý tập trung ở `GlobalExceptionHandler`.

## Package chính
| Package | Vai trò |
|---|---|
| `config` | Cấu hình hệ thống (OpenAPI, Security) |
| `controller` | Tầng REST, ánh xạ HTTP |
| `service` / `service.impl` | Business logic |
| `repository` | Spring Data JPA |
| `entity` | Ánh xạ bảng dữ liệu |
| `dto` | Đối tượng truyền dữ liệu vào/ra |
| `mapper` | Chuyển đổi Entity ↔ DTO |
| `exception` | Ngoại lệ + handler tập trung |
| `security` | JWT, filter xác thực |
| `enums` | Role, các loại Status |

## Phase 1 đã hiện thực
`FootballBookingApplication`, `HealthController`, `ApiResponse`, `HealthResponse`,
`OpenApiConfig`, nhóm exception + `GlobalExceptionHandler`.
