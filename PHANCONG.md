# PHÂN CÔNG NHÓM

> Dự án: **Hệ thống quản lý và đặt sân bóng** — Backend Spring Boot (Modular Monolith).
> Đề tài học phần: **Phát triển phần mềm hướng dịch vụ (SOA)**.
> Nhóm gồm **4 thành viên**. Chỉ có **một** Backend Spring Boot duy nhất — không chia thành 4 project riêng.

Tên thành viên hiện là **tạm thời**, sẽ được cập nhật lại trong file này khi nhóm chốt danh sách thật.

---

## 1. Tổng quan phân công

| Thành viên | Module phụ trách | Nhánh Git |
|---|---|---|
| Thành viên 1 | Authentication + User + Security | `feature/auth-user` |
| Thành viên 2 | Field + FieldType + Owner | `feature/field-owner` |
| Thành viên 3 | Booking (Availability, Price, Status) | `feature/booking` |
| Thành viên 4 | Admin + Review + Statistics | `feature/admin-review` |

---

## 2. Thành viên 1 — Authentication + User + Security

### Module
Authentication, User, JWT, Spring Security, Authorization (nền tảng).

### Backend (class phụ trách)
- `entity/User`
- `repository/UserRepository`
- `service/AuthService`, `service/UserService` (+ `impl`)
- `security/JwtService`, `security/JwtAuthenticationFilter`, `config/SecurityConfig`
- `controller/AuthController`, `controller/UserController`
- DTO: `dto/auth` (RegisterRequest, LoginRequest, LoginResponse), `dto/user` (UserResponse, UpdateUserRequest)

### API
| Method | Endpoint | Mô tả | Quyền |
|---|---|---|---|
| POST | `/api/auth/register` | Đăng ký tài khoản | Public |
| POST | `/api/auth/login` | Đăng nhập, trả JWT | Public |
| GET | `/api/users/me` | Xem thông tin bản thân | Đã đăng nhập |
| PUT | `/api/users/me` | Cập nhật thông tin bản thân | Đã đăng nhập |

### Frontend (Next.js)
`/login`, `/register`, `/profile`

### Database liên quan
Bảng `users` (email UNIQUE, password mã hóa BCrypt).

---

## 3. Thành viên 2 — Field + Owner

### Module
Field, FieldType, quản lý sân của Owner.

### Backend (class phụ trách)
- `entity/Field`, `entity/FieldType`
- `repository/FieldRepository`, `repository/FieldTypeRepository`
- `service/FieldService` (+ `impl`)
- `controller/FieldController`, `controller/OwnerFieldController`
- DTO: `dto/field` (CreateFieldRequest, UpdateFieldRequest, FieldResponse)
- `mapper/FieldMapper`

### API
| Method | Endpoint | Mô tả | Quyền |
|---|---|---|---|
| GET | `/api/fields` | Danh sách sân (search + phân trang) | Public |
| GET | `/api/fields/{id}` | Chi tiết sân | Public |
| POST | `/api/fields` | Tạo sân | OWNER/ADMIN |
| PUT | `/api/fields/{id}` | Sửa sân | Chủ sở hữu sân |
| DELETE | `/api/fields/{id}` | Xóa sân | Chủ sở hữu sân |
| GET | `/api/owner/fields` | Sân của owner hiện tại | OWNER |
| POST | `/api/owner/fields` | Tạo sân cho owner hiện tại | OWNER |
| PUT | `/api/owner/fields/{id}` | Sửa sân của owner | Chủ sở hữu sân |
| DELETE | `/api/owner/fields/{id}` | Xóa sân của owner | Chủ sở hữu sân |

### Frontend (Next.js)
`/fields`, `/fields/[id]`, `/owner/fields`, `/owner/fields/create`, `/owner/fields/[id]/edit`

### Business rule bắt buộc
Kiểm tra **quyền sở hữu tài nguyên** (resource ownership), KHÔNG chỉ kiểm tra `ROLE_OWNER`:

```
field.owner.id == currentUser.id
```

> Owner B **không được** sửa/xóa sân của Owner A. Đây là điểm chống lỗi **IDOR / Broken Access Control**.

### Database liên quan
Bảng `fields`, `field_types`. Quan hệ: `Owner 1:N Field`, `FieldType 1:N Field`.

---

## 4. Thành viên 3 — Booking (module quan trọng nhất)

### Module
Booking, Availability, Price Calculation, Booking Status.

### Backend (class phụ trách)
- `entity/Booking`
- `repository/BookingRepository`
- `service/BookingService` (+ `impl`)
- `controller/BookingController`, `controller/OwnerBookingController`
- DTO: `dto/booking` (CreateBookingRequest, BookingResponse)

### API
| Method | Endpoint | Mô tả | Quyền |
|---|---|---|---|
| POST | `/api/bookings` | Tạo booking | USER |
| GET | `/api/bookings/my` | Booking của tôi | USER |
| GET | `/api/bookings/{id}` | Chi tiết booking | Chủ booking / Owner sân / ADMIN |
| POST | `/api/bookings/{id}/cancel` | Hủy booking | Chủ booking |
| POST | `/api/owner/bookings/{id}/confirm` | Xác nhận | Owner của sân |
| POST | `/api/owner/bookings/{id}/reject` | Từ chối | Owner của sân |

### Frontend (Next.js)
`/booking`, `/my-bookings`, `/booking/[id]`

### Business logic bắt buộc (thứ tự xử lý trong Service)
1. Xác định User hiện tại
2. Kiểm tra Field tồn tại
3. Kiểm tra Field đang ACTIVE
4. Kiểm tra ngày đặt hợp lệ (không đặt quá khứ)
5. Kiểm tra `startTime` < `endTime`
6. Kiểm tra trùng lịch (overlap) trên cùng sân
7. Tính thời lượng (số giờ)
8. **Tự tính** `totalPrice = pricePerHour × số giờ`
9. Tạo Booking
10. Lưu Database
11. Trả Response

> **Tuyệt đối không tin** `totalPrice` do Frontend gửi lên — Backend luôn tự tính.
> Phải xử lý **double booking** ở mức concurrency (transaction + constraint/lock), không chỉ `existsBy...`.

### Database liên quan
Bảng `bookings`. Status: `PENDING`, `CONFIRMED`, `CANCELLED`, `REJECTED`, `COMPLETED`.

---

## 5. Thành viên 4 — Admin + Review + Statistics

### Module
Admin, Review, Statistics, System Management.

### Backend (class phụ trách)
- `entity/Review`, `entity/Payment` (thiết kế cơ bản)
- `repository/ReviewRepository`, `repository/PaymentRepository`
- `service/ReviewService`, `service/AdminService`, `service/StatisticsService` (+ `impl`)
- `controller/AdminController`, `controller/ReviewController`
- DTO: `dto/review`, `dto/admin`

### API
| Method | Endpoint | Mô tả | Quyền |
|---|---|---|---|
| GET | `/api/admin/users` | Danh sách user | ADMIN |
| GET | `/api/admin/owners` | Danh sách owner | ADMIN |
| GET | `/api/admin/fields` | Danh sách sân | ADMIN |
| GET | `/api/admin/bookings` | Danh sách booking | ADMIN |
| PUT | `/api/admin/users/{id}/status` | Đổi trạng thái user | ADMIN |
| PUT | `/api/admin/fields/{id}/status` | Đổi trạng thái sân | ADMIN |
| GET | `/api/admin/statistics` | Thống kê hệ thống | ADMIN |
| POST | `/api/reviews` | Tạo đánh giá | USER đã đặt sân |
| GET | `/api/fields/{id}/reviews` | Đánh giá của sân | Public |

### Frontend (Next.js)
`/admin`, `/admin/users`, `/admin/owners`, `/admin/fields`, `/admin/bookings`, `/admin/statistics`

### Business rule bắt buộc
Chỉ User đã có **booking hợp lệ** (đã hoàn thành) với sân mới được đánh giá sân đó.

### Database liên quan
Bảng `reviews`, `payments`.

---

## 6. Công việc chung của cả nhóm

Cả nhóm cùng thống nhất và tuân thủ (không ai tự ý thay đổi):

- Project structure (xem `README.md` mục Folder Structure)
- Database schema (xem `docs/database.md`)
- REST API convention (danh từ số nhiều, đúng HTTP method)
- DTO convention (mỗi tình huống một DTO, không dùng chung một DTO cho mọi việc)
- Response format: `ApiResponse<T>` thống nhất
- Exception handling: `GlobalExceptionHandler` tập trung
- Security: JWT + Spring Security + kiểm tra resource ownership
- Git convention (mục 7, 8)
- Testing: JUnit 5 + Mockito + Spring Boot Test
- Code Review: Alibaba Open Code Review (xem `docs/code-review.md`)
- Integration: ghép các module trên cùng một Backend

---

## 7. Git Branch

```
main        ← nhánh phát hành, luôn ổn định, KHÔNG code trực tiếp
develop     ← nhánh tích hợp, merge feature vào đây trước
  ├── feature/auth-user       (Thành viên 1)
  ├── feature/field-owner     (Thành viên 2)
  ├── feature/booking         (Thành viên 3)
  └── feature/admin-review    (Thành viên 4)
```

Quy ước commit message:

```
feat:     thêm tính năng mới        → feat: add field CRUD API
fix:      sửa lỗi                    → fix: prevent duplicate booking
refactor: tái cấu trúc, không đổi hành vi
test:     thêm/sửa test
docs:     cập nhật tài liệu
chore:    việc lặt vặt (config, build)
```

---

## 8. Quy trình Pull Request

1. Tạo nhánh `feature/*` từ `develop`.
2. Code + comment tiếng Việt + viết test.
3. `mvn test` phải PASS ở máy cá nhân trước khi push.
4. Mở Pull Request vào `develop`, mô tả rõ thay đổi.
5. Chạy **Alibaba Open Code Review** trên phần thay đổi.
6. Ít nhất **1 thành viên khác** review và duyệt.
7. Sửa hết finding nghiêm trọng → review lại → merge.
8. Định kỳ merge `develop` → `main` khi đạt mốc ổn định.

---

## 9. Code Review

Mỗi feature bắt buộc đi qua chu trình:

```
Implement → Compile → Unit Test → Integration Test
   → Alibaba Open Code Review → Phân tích findings
   → Fix → Test lại → Review lại
```

Checklist review tối thiểu: Correctness, Security (SQL Injection, IDOR, Broken Access Control,
Password/JWT Exposure), Performance (N+1, Race Condition), Maintainability, SOLID, REST/JPA,
Exception Handling, Validation, HTTP Status, Transaction, Naming, Complexity, Duplicated Code.

> Không tự ý xóa finding chỉ để báo cáo đẹp. Chỉ ghi **Code Review: PASS** khi thực sự đã review.

---

## 10. Definition of Done

Một feature chỉ được xem là **hoàn thành** khi:

- [ ] Code compile thành công
- [ ] Application chạy được
- [ ] API hoạt động đúng
- [ ] Validation hoạt động
- [ ] Exception handling hoạt động
- [ ] HTTP status trả về đúng
- [ ] Unit test pass
- [ ] Integration test pass (nếu cần)
- [ ] Security được kiểm tra
- [ ] Authorization được kiểm tra
- [ ] Alibaba Open Code Review đã chạy
- [ ] Các finding nghiêm trọng đã được xử lý
- [ ] Đã được thành viên khác review lại
- [ ] Documentation được cập nhật
- [ ] Đã Pull Request và merge
