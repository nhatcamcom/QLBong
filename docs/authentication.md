# Xác thực (Authentication)

> **Đã triển khai.** JWT stateless + Spring Security + BCrypt.

## Đăng ký — `POST /api/auth/register`
Request:
```json
{ "fullName": "Nguyen Van A", "email": "user@gmail.com", "password": "123456", "phone": "0901234567" }
```
- Mật khẩu **bắt buộc** mã hóa bằng **BCrypt** trước khi lưu (không lưu plaintext).
- Email trùng → `409 Conflict`.

## Đăng nhập — `POST /api/auth/login`
Response khi thành công:
```json
{
  "status": 200,
  "message": "Đăng nhập thành công",
  "data": {
    "accessToken": "<JWT>",
    "tokenType": "Bearer",
    "user": { "id": 1, "fullName": "Nguyen Van A", "role": "USER" }
  }
}
```

## JWT
- Frontend gửi kèm mọi request cần xác thực: `Authorization: Bearer <JWT>`.
- **Khóa ký (`JWT_SECRET`) không hard-code** — đọc từ biến môi trường.
- Filter `JwtAuthenticationFilter` sẽ đọc token, xác thực và nạp thông tin người dùng vào `SecurityContext`.
- Thiếu/hết hạn token trên endpoint bảo mật → `401 Unauthorized`.
