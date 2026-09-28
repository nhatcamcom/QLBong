# Phân quyền (Authorization)

> **Đã triển khai.** Chặn thô theo vai trò tại `SecurityConfig`, kiểm tra quyền sở hữu tại tầng Service.

## Ba vai trò
| Vai trò | Được phép |
|---|---|
| `USER` | Xem sân, đặt sân, xem/hủy booking của mình, đánh giá sân đã đặt, quản lý profile |
| `OWNER` | Quản lý sân của mình, xem booking sân của mình, confirm/reject, xem thống kê |
| `ADMIN` | Quản lý toàn bộ hệ thống |

Không tự ý tạo thêm vai trò khác.

## Hai lớp kiểm soát
1. **Role-based** — chặn theo vai trò (ví dụ chỉ `ADMIN` gọi `/api/admin/**`).
2. **Resource ownership** — kiểm tra chủ sở hữu tài nguyên, chống **IDOR / Broken Access Control**:

```java
// KHÔNG chỉ kiểm tra ROLE_OWNER mà phải kiểm tra đúng chủ sở hữu
if (!field.getOwner().getId().equals(currentUser.getId())) {
    throw new AccessDeniedException("Bạn không có quyền thao tác trên sân này");
}
```

## Ví dụ tình huống bảo mật
- Owner B gọi `PUT /api/fields/1` (sân của Owner A) → `403 Forbidden`.
- USER gọi API `/api/admin/**` → `403 Forbidden`.
- Không có JWT trên endpoint bảo mật → `401 Unauthorized`.

## Điểm rà soát bảo mật bắt buộc
Authentication, Authorization, Resource ownership, IDOR, Broken Access Control,
Password exposure, JWT secret exposure.
