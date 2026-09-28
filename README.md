# Football Booking Backend

Backend cho **Hệ thống quản lý và đặt sân bóng**, xây dựng theo định hướng học phần
**Phát triển phần mềm hướng dịch vụ (SOA)**.

> Trạng thái: **Hoàn thiện toàn bộ backend** — Auth/JWT/Security, User, Field/Owner, Booking
> (tự tính tiền, chống đặt trùng), Review, Admin/Statistics, Payment cơ bản, tìm kiếm & phân trang,
> Swagger. 23 unit/integration test PASS.

---

## 1. Project Overview
Hệ thống cho phép người dùng tìm và đặt sân bóng, chủ sân (Owner) quản lý sân và
xác nhận đơn đặt, quản trị viên (Admin) quản lý toàn hệ thống và xem thống kê.
Backend cung cấp **RESTful API** cho Frontend **Next.js**.

Luồng kiến trúc theo tài liệu học phần:

```
Business Function → Service → REST API → HTTP Request
→ Controller → Service Layer → Repository → MySQL → HTTP Response
```

## 2. Technology Stack
- Java 17, Spring Boot 3.3.5, Maven
- Spring Web, Spring Data JPA (Hibernate), Spring Validation
- Spring Security + JWT *(từ Phase 2)*
- MySQL 8 (production) / H2 in-memory (test)
- OpenAPI / Swagger (springdoc)
- JUnit 5, Mockito, Spring Boot Test

## 3. Architecture
Kiến trúc phân tầng nghiêm ngặt, **không cho phép** Controller gọi thẳng Repository:

```
Controller  →  Service (business logic)  →  Repository  →  MySQL
```

Đây là **Modular Monolith** — một ứng dụng Spring Boot duy nhất, chưa tách Microservices.
Chi tiết: [docs/architecture.md](docs/architecture.md).

## 4. Folder Structure
```
football-booking-backend/
├── src/main/java/com/example/footballbooking/
│   ├── config/         # Cấu hình (OpenAPI, Security...)
│   ├── controller/     # Nhận HTTP request, trả response
│   ├── service/impl/   # Business logic
│   ├── repository/     # Truy cập dữ liệu (Spring Data JPA)
│   ├── entity/         # Thực thể ánh xạ bảng MySQL
│   ├── dto/            # Request/Response DTO (auth,user,field,booking,review,admin)
│   ├── mapper/         # Chuyển đổi Entity <-> DTO
│   ├── exception/      # Exception + GlobalExceptionHandler
│   ├── security/       # JWT filter, JwtService (Phase 2)
│   ├── enums/          # Role, Status...
│   └── FootballBookingApplication.java
├── src/main/resources/ # application.yml, application-dev.yml
├── src/test/           # Unit + Integration test
├── docs/               # Tài liệu chi tiết
├── PHANCONG.md         # Phân công nhóm 4 người
├── pom.xml
└── README.md
```

## 5. Database
Database MySQL: `football_booking`. Các bảng chính: `users`, `field_types`, `fields`,
`bookings`, `reviews`, `payments`. Schema chi tiết & quan hệ: [docs/database.md](docs/database.md).
Script SQL import thủ công (schema + dữ liệu mẫu): **[docs/database.sql](docs/database.sql)** —
`mysql -u root -p < docs/database.sql`. (Mặc định app dùng `ddl-auto: update` nên cũng tự tạo bảng.)

## 6. API
Tài liệu API: [docs/api.md](docs/api.md) và Swagger UI (mục 11). Các nhóm endpoint:
`/api/auth`, `/api/users`, `/api/fields`, `/api/field-types`, `/api/owner/fields`,
`/api/bookings`, `/api/owner/bookings`, `/api/reviews`, `/api/admin`, `/api/health`.

## 7. Authentication
Đăng ký/đăng nhập trả JWT; Frontend gửi kèm header `Authorization: Bearer <JWT>`.
Chi tiết: [docs/authentication.md](docs/authentication.md) *(triển khai ở Phase 2)*.

## 8. Authorization
Ba vai trò: `USER`, `OWNER`, `ADMIN`. Ngoài kiểm tra vai trò còn kiểm tra
**quyền sở hữu tài nguyên** (`resource.owner.id == currentUser.id`) để chống IDOR.
Chi tiết: [docs/authorization.md](docs/authorization.md).

## 9. Environment Variables
Không hard-code bí mật. Cấu hình qua **file `.env`** ở thư mục gốc (ứng dụng tự đọc nhờ
`spring.config.import` trong `application.yml`) hoặc qua biến môi trường hệ điều hành.

```bash
cp .env.example .env   # rồi sửa DB_PASSWORD, JWT_SECRET... cho khớp máy bạn
```
> `.env` đã được `.gitignore` (không commit secret). Quy tắc: KHÔNG dùng dấu nháy quanh giá trị,
> KHÔNG có khoảng trắng quanh dấu `=`. Biến môi trường hệ điều hành (nếu có) sẽ ưu tiên hơn `.env`.

Các biến (đều có giá trị mặc định cho dev nếu thiếu):

| Biến | Ý nghĩa | Mặc định (dev) |
|---|---|---|
| `DB_URL` | JDBC URL của MySQL | `jdbc:mysql://localhost:3306/football_booking?...` |
| `DB_USERNAME` | Tài khoản MySQL | `root` |
| `DB_PASSWORD` | Mật khẩu MySQL | *(rỗng)* |
| `SERVER_PORT` | Cổng chạy ứng dụng | `8080` |
| `SPRING_PROFILES_ACTIVE` | Profile | `dev` |
| `JWT_SECRET` | Khóa ký JWT | *(bắt buộc từ Phase 2)* |

## 10. How to Run
Yêu cầu: Java 17, Maven, MySQL 8 đang chạy.

```bash
# 1) (Tùy chọn) đặt biến môi trường DB nếu khác mặc định
export DB_USERNAME=root DB_PASSWORD=yourpassword

# 2) Chạy ứng dụng
mvn spring-boot:run

# 3) Kiểm tra hoạt động
curl http://localhost:8080/api/health
# => {"status":"UP","service":"Football Booking Service"}
```

Khi chạy với profile `dev`, hệ thống tự tạo dữ liệu mẫu (tài khoản ADMIN/OWNER không thể
đăng ký qua API). Tài khoản demo (đổi mật khẩu trước khi lên production):

| Vai trò | Email | Mật khẩu |
|---|---|---|
| ADMIN | `admin@footballbooking.com` | `Admin@123` |
| OWNER | `owner@footballbooking.com` | `Owner@123` |
| USER | `user@footballbooking.com` | `User@123` |

Luồng thử nhanh: `POST /api/auth/login` lấy `accessToken` → gắn header `Authorization: Bearer <token>`
cho các API cần xác thực (đặt sân, quản lý sân, admin...).

## 11. Swagger
Sau khi chạy ứng dụng:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 12. Postman
Collection Postman sẽ được bổ sung khi các API nghiệp vụ hoàn thiện (Phase 7).

## 13. Testing
```bash
mvn test
```
23 test (unit + integration) chạy trên H2 in-memory (profile `test`) nên không cần MySQL.
Bao phủ: booking (tính tiền, trùng lịch, field không tồn tại/inactive, hủy), quyền sở hữu sân,
đăng ký/đăng nhập, phân quyền 401/403, validation 400. Chi tiết: [docs/testing.md](docs/testing.md).

## 14. Git Workflow
`main` (phát hành) ← `develop` (tích hợp) ← `feature/*`. Xem [PHANCONG.md](PHANCONG.md) mục 7–8.

## 15. Alibaba Open Code Review
Dự án dùng [alibaba/open-code-review](https://github.com/alibaba/open-code-review) (`ocr`)
để review tự động. Cách cài đặt, cấu hình, cách chạy và báo cáo: [docs/code-review.md](docs/code-review.md).

## 16. Team Assignment
Phân công 4 thành viên: [PHANCONG.md](PHANCONG.md).
