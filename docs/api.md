# Tài liệu API

## Quy ước chung
- Base path: `/api`
- Định dạng: JSON. Header khi cần xác thực: `Authorization: Bearer <JWT>`.
- Response bọc trong `ApiResponse<T>` (trừ `/api/health`).

### Định dạng phản hồi thành công
```json
{ "status": 200, "message": "Thành công", "data": { } }
```
### Định dạng phản hồi lỗi
```json
{ "status": 404, "message": "Không tìm thấy sân bóng", "timestamp": "2026-09-28T10:00:00" }
```
### Lỗi validation (kèm chi tiết theo trường)
```json
{ "status": 400, "message": "Dữ liệu không hợp lệ",
  "timestamp": "2026-09-28T10:00:00",
  "errors": { "name": "Tên sân không được để trống" } }
```

## HTTP Status quy ước
`200` OK · `201` Created · `204` No Content · `400` Bad Request · `401` Unauthorized ·
`403` Forbidden · `404` Not Found · `409` Conflict · `500` Internal Server Error.

## Endpoint theo Phase

### Toàn bộ endpoint (đã hoạt động)
| Nhóm | Endpoint | Quyền |
|---|---|---|
| Health | `GET /api/health` | Public |
| Auth | `POST /api/auth/register`, `POST /api/auth/login` | Public |
| User | `GET /api/users/me`, `PUT /api/users/me` | Đã đăng nhập |
| FieldType | `GET /api/field-types` | Public |
| Field | `GET /api/fields` (search+page), `GET /api/fields/{id}` | Public |
| Field | `POST /api/fields`, `PUT/DELETE /api/fields/{id}` | OWNER/ADMIN + chủ sở hữu |
| Owner Field | `GET/POST /api/owner/fields`, `PUT/DELETE /api/owner/fields/{id}` | OWNER (chủ sở hữu) |
| Booking | `POST /api/bookings`, `GET /api/bookings/my`, `GET /api/bookings/{id}`, `POST /api/bookings/{id}/cancel` | Đã đăng nhập |
| Owner Booking | `GET /api/owner/bookings`, `POST /api/owner/bookings/{id}/confirm`, `.../reject` | OWNER (chủ sân) |
| Review | `POST /api/reviews`, `GET /api/fields/{id}/reviews` | Tạo: đã đăng nhập; Xem: public |
| Admin | `GET /api/admin/users\|owners\|fields\|bookings\|statistics`, `PUT /api/admin/users/{id}/status`, `PUT /api/admin/fields/{id}/status` | ADMIN |

Tham số tìm kiếm sân: `keyword`, `address`, `fieldType`, `minPrice`, `maxPrice`, `page`, `size`.

> Tài liệu tương tác đầy đủ xem tại Swagger UI: `/swagger-ui.html`.
