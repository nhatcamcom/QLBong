-- ==========================================================================
-- Football Booking - Script khởi tạo cơ sở dữ liệu MySQL
-- ==========================================================================
-- Cách dùng:
--   1) Import file này vào MySQL:  mysql -u root -p < docs/database.sql
--      (hoặc mở bằng phpMyAdmin / MySQL Workbench rồi chạy)
--   2) Nếu muốn DB làm chủ schema (không để Hibernate tự tạo), đặt trong
--      application.yml:  spring.jpa.hibernate.ddl-auto: validate  (hoặc none)
--
-- Ghi chú: schema dưới đây khớp với các entity JPA. Nếu vẫn để ddl-auto=update
-- thì ứng dụng cũng tự tạo được các bảng này; file SQL phục vụ import thủ công
-- và để nộp/bàn giao.
-- ==========================================================================

CREATE DATABASE IF NOT EXISTS football_booking
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE football_booking;

-- Xóa theo thứ tự ngược khóa ngoại để có thể chạy lại nhiều lần
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS fields;
DROP TABLE IF EXISTS field_types;
DROP TABLE IF EXISTS users;

-- ------------------------------------------------------------------
-- Bảng người dùng
-- ------------------------------------------------------------------
CREATE TABLE users (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    full_name   VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    password    VARCHAR(255) NOT NULL,                 -- luôn lưu dạng BCrypt
    phone       VARCHAR(20),
    role        VARCHAR(20)  NOT NULL,
    status      VARCHAR(20)  NOT NULL,
    created_at  DATETIME(6),
    updated_at  DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT chk_users_role   CHECK (role   IN ('USER','OWNER','ADMIN')),
    CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE','INACTIVE','BLOCKED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------
-- Bảng loại sân
-- ------------------------------------------------------------------
CREATE TABLE field_types (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    status      VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_field_types_status CHECK (status IN ('ACTIVE','INACTIVE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------
-- Bảng sân bóng
-- ------------------------------------------------------------------
CREATE TABLE fields (
    id             BIGINT        NOT NULL AUTO_INCREMENT,
    owner_id       BIGINT        NOT NULL,
    field_type_id  BIGINT        NOT NULL,
    name           VARCHAR(255)  NOT NULL,
    address        VARCHAR(255)  NOT NULL,
    description    VARCHAR(1000),
    price_per_hour DECIMAL(12,2) NOT NULL,
    image_url      VARCHAR(500),
    status         VARCHAR(20)   NOT NULL,
    created_at     DATETIME(6),
    updated_at     DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_fields_owner      FOREIGN KEY (owner_id)      REFERENCES users(id),
    CONSTRAINT fk_fields_field_type FOREIGN KEY (field_type_id) REFERENCES field_types(id),
    CONSTRAINT chk_fields_status CHECK (status IN ('ACTIVE','INACTIVE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
-- Ghi chú: InnoDB tự tạo index cho các cột khóa ngoại (owner_id, field_type_id).

-- ------------------------------------------------------------------
-- Bảng đặt sân
-- ------------------------------------------------------------------
CREATE TABLE bookings (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    user_id      BIGINT        NOT NULL,
    field_id     BIGINT        NOT NULL,
    booking_date DATE          NOT NULL,
    start_time   TIME          NOT NULL,
    end_time     TIME          NOT NULL,
    total_price  DECIMAL(12,2) NOT NULL,               -- Backend tự tính
    status       VARCHAR(20)   NOT NULL,
    created_at   DATETIME(6),
    updated_at   DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_bookings_user  FOREIGN KEY (user_id)  REFERENCES users(id),
    CONSTRAINT fk_bookings_field FOREIGN KEY (field_id) REFERENCES fields(id),
    CONSTRAINT chk_bookings_status
        CHECK (status IN ('PENDING','CONFIRMED','CANCELLED','REJECTED','COMPLETED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Index hỗ trợ kiểm tra trùng lịch theo sân + ngày
CREATE INDEX idx_booking_field_date ON bookings (field_id, booking_date);

-- ------------------------------------------------------------------
-- Bảng đánh giá (mỗi booking chỉ đánh giá 1 lần)
-- ------------------------------------------------------------------
CREATE TABLE reviews (
    id         BIGINT        NOT NULL AUTO_INCREMENT,
    user_id    BIGINT        NOT NULL,
    field_id   BIGINT        NOT NULL,
    booking_id BIGINT        NOT NULL,
    rating     INT           NOT NULL,
    comment    VARCHAR(1000),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_reviews_booking UNIQUE (booking_id),
    CONSTRAINT fk_reviews_user    FOREIGN KEY (user_id)    REFERENCES users(id),
    CONSTRAINT fk_reviews_field   FOREIGN KEY (field_id)   REFERENCES fields(id),
    CONSTRAINT fk_reviews_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),
    CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------
-- Bảng thanh toán (mức cơ bản, 1-1 với booking)
-- ------------------------------------------------------------------
CREATE TABLE payments (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    booking_id       BIGINT        NOT NULL,
    amount           DECIMAL(12,2) NOT NULL,
    payment_method   VARCHAR(20)   NOT NULL,
    status           VARCHAR(20)   NOT NULL,
    transaction_code VARCHAR(100),
    created_at       DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_payments_booking UNIQUE (booking_id),
    CONSTRAINT fk_payments_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),
    CONSTRAINT chk_payments_method CHECK (payment_method IN ('CASH','BANK_TRANSFER')),
    CONSTRAINT chk_payments_status CHECK (status IN ('PENDING','PAID','FAILED','REFUNDED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================================================
-- DỮ LIỆU MẪU
-- Mật khẩu đã băm BCrypt. Đăng nhập tương ứng:
--   admin@footballbooking.com / Admin@123
--   owner@footballbooking.com / Owner@123
--   user@footballbooking.com  / User@123
-- ==========================================================================
INSERT INTO field_types (name, description, status) VALUES
    ('Sân 5 người',  'Sân bóng đá mini 5 người',          'ACTIVE'),
    ('Sân 7 người',  'Sân bóng đá 7 người',               'ACTIVE'),
    ('Sân 11 người', 'Sân bóng đá 11 người tiêu chuẩn',   'ACTIVE');

INSERT INTO users (full_name, email, password, phone, role, status, created_at, updated_at) VALUES
    ('Quản trị viên', 'admin@footballbooking.com', '$2a$10$IFhC8zAz2ZKxbOCsJDRymOiIHiBZct5D2LtEunxfjL/.WHxdoSANi', '0900000000', 'ADMIN', 'ACTIVE', NOW(6), NOW(6)),
    ('Chủ sân demo',  'owner@footballbooking.com', '$2a$10$FqnVCy8.6Z1lAuIM2Gbw2uZn/FRlqXTHHS6R9SPCsRqfqdqtf6dgW', '0900000000', 'OWNER', 'ACTIVE', NOW(6), NOW(6)),
    ('Người dùng demo','user@footballbooking.com', '$2a$10$yGvPkv2ZV4PxOa6EC3g9H.s4A2HWFkvZLkUDudI/Bj3Wv5GVe/tqS', '0900000000', 'USER',  'ACTIVE', NOW(6), NOW(6));

-- Sân mẫu thuộc chủ sân demo, loại "Sân 7 người" (dùng INSERT..SELECT để lấy đúng khóa ngoại)
INSERT INTO fields (owner_id, field_type_id, name, address, description, price_per_hour, status, created_at, updated_at)
SELECT u.id, ft.id,
       'Sân bóng Ngôi Sao',
       '123 Nguyễn Trãi, Hà Nội',
       'Sân cỏ nhân tạo, có mái che và đèn chiếu sáng',
       300000.00,
       'ACTIVE',
       NOW(6), NOW(6)
FROM users u
JOIN field_types ft ON ft.name = 'Sân 7 người'
WHERE u.email = 'owner@footballbooking.com';
