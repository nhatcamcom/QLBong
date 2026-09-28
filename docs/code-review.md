# Code Review — Alibaba Open Code Review

Dự án dùng công cụ chính thức **[alibaba/open-code-review](https://github.com/alibaba/open-code-review)**
(lệnh `ocr`) để review tự động. Tài liệu này ghi lại cách cài đặt/cấu hình/chạy **đã kiểm chứng thực tế**
trên máy dự án, cùng báo cáo review cho từng Phase.

## 1. Công cụ (đã xác minh)
- Repo: https://github.com/alibaba/open-code-review · License: Apache-2.0
- Cài qua npm: package `@alibaba-group/open-code-review`, cung cấp lệnh `ocr`
- Phiên bản đã cài trên máy dự án: **v1.12.9** (windows/amd64)
- Yêu cầu: **Git >= 2.41** (máy dự án: Git 2.55 ✓); Node.js + npm để cài đặt

## 2. Cài đặt
```bash
npm install -g @alibaba-group/open-code-review
```
> Lưu ý thực tế đã gặp:
> - **npm 11** chặn install script theo mặc định. Cần chạy:
>   `npm install -g --allow-scripts=@alibaba-group/open-code-review @alibaba-group/open-code-review`
>   để postinstall tải đúng binary nền tảng (`@alibaba-group/ocr-win32-x64`).
> - Trên Windows, hãy cài từ **PowerShell/CMD** (PATH chuẩn Windows), không cài từ Git Bash —
>   postinstall gọi `node` qua `cmd.exe`; PATH kiểu POSIX của Git Bash khiến `cmd.exe` không tìm thấy `node`.

Kiểm tra: `ocr --version`.

## 3. Cấu hình
Hai chế độ chạy:

**(a) Chế độ mặc định (OCR tự gọi LLM)** — cần cấu hình một nhà cung cấp LLM + API key:
```bash
ocr config provider   # chọn provider (OpenAI, Anthropic, custom...) và nhập API key
ocr config model      # chọn model
```

**(b) Chế độ ủy quyền — Delegation (KHÔNG cần API key):**
OCR chỉ lo phần chọn file + phân giải rule, còn phần "đọc và tìm lỗi" do chính AI coding agent thực hiện.
```bash
ocr delegate preview --from <base> --to <target>   # xem file nào sẽ được review
ocr delegate rule <file1> <file2> ...              # lấy bộ rule áp dụng cho các file
```

## 4. Cách chạy review
```bash
ocr review                                   # review thay đổi trong workspace
ocr review --from develop --to feature/booking   # review theo nhánh
ocr review --commit <hash>                   # review một commit
ocr scan                                     # quét toàn bộ file (không cần diff)
ocr review --format json --output ocr-result.json   # lưu kết quả ra file
```

---

## 5. Báo cáo review — PHASE 1

**Phạm vi:** toàn bộ mã nguồn Java của Phase 1 (health API, `ApiResponse`, exception handling, cấu hình).

**Cách thực hiện (trung thực):**
- Đã cài `ocr` v1.12.9 và chạy **Delegation mode**: `ocr delegate rule <các file .java>` →
  OCR phân giải bộ rule chuẩn cho nhóm `system / **/*.java` gồm: *Typos, Dead Code, Logic Error Detection,
  Severe Performance Issues (N+1, query trong vòng lặp), Thread Safety*.
- Phần phân tích lỗi được **host agent (Claude Opus 4.8) thực hiện theo đúng cơ chế delegation** của OCR.
- **Chưa chạy** chế độ mặc định (OCR tự gọi LLM) vì cần cấu hình provider + API key của nhóm.
  → Không "giả vờ" đã chạy chế độ này. Khi nhóm cấu hình `ocr config provider/model`, có thể chạy lại
  `ocr scan` / `ocr review` để đối chiếu.

| Hạng mục | Kết quả |
|---|---|
| Build (`mvn -B test`) | **PASS** |
| Unit test | **PASS** (HealthControllerTest) |
| Integration test | **PASS** (context load + error handling) |
| Alibaba OCR — Delegation | **ĐÃ CHẠY** (rules resolved) |
| Alibaba OCR — Default LLM mode | **CHƯA CHẠY** (cần API key) |

### Findings

| # | Mức độ | Rule liên quan | Mô tả | Trạng thái |
|---|---|---|---|---|
| F1 | Medium | Logic Error / Incorrect HTTP Status | `@ExceptionHandler(Exception.class)` bắt luôn ngoại lệ khung của Spring (`NoResourceFoundException`), khiến đường dẫn không tồn tại trả **500 thay vì 404**. | **ĐÃ SỬA** |

Thống kê: **Critical: 0 · High: 0 · Medium: 1 · Low: 0** — tất cả đã xử lý.

### Chi tiết F1 (kèm bằng chứng)
- **Phát hiện:** test `GlobalExceptionHandlerIntegrationTest.unknownPath_shouldReturnNotFound` ban đầu thất bại:
  `Status expected:<404> but was:<500>` khi gọi `GET /api/khong-ton-tai`.
- **Nguyên nhân:** handler tổng quát `Exception.class` được ưu tiên trước bộ xử lý mặc định của Spring MVC,
  nuốt luôn các ngoại lệ khung nên trả sai status.
- **Ảnh hưởng:** mọi lỗi khung (404 sai đường dẫn, 405 sai method, 400 body hỏng) đều bị biến thành 500 —
  vi phạm nguyên tắc "dùng đúng HTTP status".
- **Cách sửa:** bổ sung handler riêng trước handler tổng quát:
  `NoResourceFoundException → 404`, `HttpRequestMethodNotSupportedException → 405`,
  `HttpMessageNotReadableException → 400`; giữ `Exception.class → 500` làm phương án cuối.
- **Kiểm chứng lại:** chạy lại `mvn -B test` → **4/4 PASS**, đường dẫn không tồn tại trả đúng 404.

### Ghi chú (không phải lỗi)
- Log console trên Windows hiển thị tiếng Việt bị lỗi font; phản hồi HTTP thực tế là UTF-8 (mặc định Spring Boot).
  Đây là hạn chế của console, không phải lỗi ứng dụng.
- Bộ rule mặc định `**/*.java` chưa bao gồm rule bảo mật chuyên sâu (SQL Injection, IDOR, JWT exposure).
  Các rule này sẽ quan trọng từ Phase 2+ (khi có DB/Security); có thể bổ sung custom rule cho OCR khi đó.

---

## 6. Báo cáo review — TOÀN HỆ THỐNG (Auth, Field, Booking, Review, Admin)

**Phạm vi:** toàn bộ mã nguồn nghiệp vụ bổ sung sau Phase 1 (diff `dc9978e..HEAD`).

**Cách thực hiện (trung thực):**
- Đã chạy `ocr delegate preview --from dc9978e --to HEAD` → OCR chọn **80 file** cần review
  (tổng +4267 dòng), chế độ `range` (merge-base).
- Đã chạy `ocr delegate rule <các file service/security/repository>` → OCR phân giải bộ rule
  `system / **/*.java`: *Typos, Dead Code, Logic Error, Severe Performance (N+1), Thread Safety*.
- Phần phân tích do host agent (Claude) thực hiện theo cơ chế delegation.
- **Chưa chạy** chế độ OCR tự gọi LLM (cần API key của nhóm).

| Hạng mục | Kết quả |
|---|---|
| Build (`mvn -B test`) | **PASS** |
| Unit test (BookingService, FieldService, AuthService) | **PASS** (15 test) |
| Integration test (auth + phân quyền + exception) | **PASS** (8 test) |
| Tổng test | **23 PASS / 0 fail** |
| Alibaba OCR — Delegation | **ĐÃ CHẠY** (preview + rule) |
| Alibaba OCR — Default LLM mode | **CHƯA CHẠY** (cần API key) |

### Findings

| # | Mức độ | Rule | Mô tả | Trạng thái |
|---|---|---|---|---|
| F2 | Medium | Logic/Concurrency | Đăng ký trùng email chạy song song có thể vi phạm unique constraint → trả 500 thay vì 409 | **ĐÃ SỬA** (thêm handler `DataIntegrityViolationException → 409`) |
| F3 | Low (info) | Performance | `StatisticsService` gọi `countByStatus` 5 lần (theo số enum) thay vì 1 câu GROUP BY | Chấp nhận: số truy vấn có chặn trên, không phải N+1 theo dữ liệu |
| F4 | Low (info) | Access control | Matcher cho `PUT/DELETE /api/fields/*` cho phép cả ADMIN, nhưng Service chỉ cho chính chủ → ADMIN không phải chủ sẽ nhận 403 | Chấp nhận: fail-safe, admin dùng endpoint `/api/admin/**` riêng |

Thống kê: **Critical 0 · High 0 · Medium 1 (đã sửa) · Low 2 (thông tin)**.

### Điểm bảo mật đã kiểm tra (đạt)
- **SQL Injection**: toàn bộ truy vấn dùng JPA/Criteria API + tham số ràng buộc (`@Param`), không nối chuỗi SQL.
- **IDOR / Broken Access Control**: kiểm tra quyền sở hữu ở Service (`field.owner.id == currentUser.id`,
  chủ booking/chủ sân với booking), không chỉ dựa vai trò.
- **Password exposure**: mật khẩu mã hóa BCrypt; DTO trả về không chứa mật khẩu; không trả Entity.
- **JWT**: khóa bí mật đọc từ biến môi trường `JWT_SECRET` (chỉ có giá trị mặc định cho dev).
- **User enumeration**: đăng nhập sai trả thông báo chung, không lộ email tồn tại hay không.
- **Double booking**: khóa bi quan dòng sân + kiểm tra overlap trong cùng transaction.
- **N+1**: bật `hibernate.default_batch_fetch_size=100`; map DTO trong transaction.

> Ghi chú môi trường: MySQL trên máy hiện đã dừng nên chưa chạy lại smoke test HTTP trực tiếp trên MySQL ở
> vòng này; toàn bộ 23 test (gồm integration test chạy qua đầy đủ filter bảo mật + controller + service + JPA)
> đã PASS trên H2. Ở Phase 1, endpoint `/api/health` đã được xác minh chạy thật trên MySQL.
