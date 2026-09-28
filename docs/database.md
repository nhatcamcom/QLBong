# Cơ sở dữ liệu (Database)

Database MySQL: **`football_booking`**. Ứng dụng đang để `ddl-auto: update` nên Hibernate tự tạo
bảng theo entity. Ngoài ra có sẵn **script SQL để import thủ công**:

> **File SQL: [database.sql](database.sql)** — CREATE DATABASE + toàn bộ bảng (khóa ngoại, index,
> ràng buộc) + dữ liệu mẫu (loại sân, tài khoản admin/owner/user, một sân mẫu).
> Import: `mysql -u root -p < docs/database.sql` (hoặc chạy trong phpMyAdmin/Workbench).
> Nếu import bằng file này, nên đặt `spring.jpa.hibernate.ddl-auto: validate` (hoặc `none`).

## Sơ đồ quan hệ
```
User  1───N  Booking        Owner(User) 1───N  Field
User  1───N  Review         FieldType   1───N  Field
Field 1───N  Booking        Field       1───N  Review
Booking 1───1 Payment
```

## Bảng `users`
| Cột | Kiểu | Ghi chú |
|---|---|---|
| id | BIGINT PK | auto increment |
| full_name | VARCHAR | |
| email | VARCHAR | **UNIQUE** |
| password | VARCHAR | mã hóa **BCrypt** (không lưu plaintext) |
| phone | VARCHAR | |
| role | ENUM | `USER`, `OWNER`, `ADMIN` |
| status | ENUM | `ACTIVE`, `INACTIVE`, `BLOCKED` |
| created_at / updated_at | DATETIME | |

## Bảng `field_types`
| Cột | Kiểu | Ghi chú |
|---|---|---|
| id | BIGINT PK | |
| name | VARCHAR | ví dụ: sân 5, sân 7, sân 11 |
| description | VARCHAR | |
| status | ENUM | ACTIVE/INACTIVE |

## Bảng `fields`
| Cột | Kiểu | Ghi chú |
|---|---|---|
| id | BIGINT PK | |
| owner_id | BIGINT FK → users | chủ sân |
| field_type_id | BIGINT FK → field_types | |
| name, address, description | VARCHAR | |
| price_per_hour | DECIMAL | dùng để Backend tự tính tiền |
| image_url | VARCHAR | |
| status | ENUM | ACTIVE/INACTIVE |
| created_at / updated_at | DATETIME | |

## Bảng `bookings`
| Cột | Kiểu | Ghi chú |
|---|---|---|
| id | BIGINT PK | |
| user_id | BIGINT FK → users | |
| field_id | BIGINT FK → fields | |
| booking_date | DATE | |
| start_time / end_time | TIME | `start_time < end_time` |
| total_price | DECIMAL | **Backend tự tính**, không tin Frontend |
| status | ENUM | PENDING, CONFIRMED, CANCELLED, REJECTED, COMPLETED |
| created_at / updated_at | DATETIME | |

> Chống **double booking**: cần chỉ mục/ràng buộc + xử lý transaction/locking khi hiện thực (Phase 4).

## Bảng `reviews`
| Cột | Kiểu | Ghi chú |
|---|---|---|
| id | BIGINT PK | |
| user_id | BIGINT FK → users | |
| field_id | BIGINT FK → fields | |
| booking_id | BIGINT FK → bookings | chỉ booking hợp lệ mới được review |
| rating | INT | 1..5 |
| comment | TEXT | |
| created_at / updated_at | DATETIME | |

## Bảng `payments` (thiết kế cơ bản)
| Cột | Kiểu | Ghi chú |
|---|---|---|
| id | BIGINT PK | |
| booking_id | BIGINT FK → bookings | |
| amount | DECIMAL | |
| payment_method | VARCHAR | |
| status | ENUM | PENDING, PAID, FAILED, REFUNDED |
| transaction_code | VARCHAR | |
| created_at | DATETIME | |

> Chưa tích hợp cổng thanh toán thật cho đến khi có yêu cầu.
