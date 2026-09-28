package com.example.footballbooking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Cấu trúc phản hồi (Response) chung cho toàn bộ REST API.
 *
 * <p>Mọi API đều trả về đối tượng này để Frontend Next.js xử lý thống nhất.
 * Các trường {@code null} sẽ bị lược bỏ khỏi JSON nhờ {@link JsonInclude}, nhờ vậy:</p>
 * <ul>
 *   <li>Phản hồi thành công: {@code { status, message, data }}</li>
 *   <li>Phản hồi lỗi: {@code { status, message, timestamp }} (kèm {@code errors} nếu là lỗi validation)</li>
 * </ul>
 *
 * @param <T> kiểu dữ liệu nghiệp vụ trả về trong trường {@code data}
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /** Mã HTTP status tương ứng (200, 201, 400, 404...). */
    private int status;

    /** Thông điệp mô tả kết quả, viết bằng tiếng Việt cho người dùng cuối. */
    private String message;

    /** Dữ liệu nghiệp vụ trả về (chỉ có ở phản hồi thành công). */
    private T data;

    /** Thời điểm phát sinh lỗi (chỉ đính kèm ở phản hồi lỗi). */
    private LocalDateTime timestamp;

    /** Danh sách lỗi theo từng trường (chỉ dùng cho lỗi validation). */
    private Map<String, String> errors;

    public ApiResponse() {
    }

    private ApiResponse(int status, String message, T data, LocalDateTime timestamp, Map<String, String> errors) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
        this.errors = errors;
    }

    // ====== Các factory method giúp tạo phản hồi ngắn gọn, tránh new thủ công ======

    /** Tạo phản hồi thành công 200 với thông điệp mặc định. */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Thành công", data, null, null);
    }

    /** Tạo phản hồi thành công 200 với thông điệp tùy chỉnh. */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data, null, null);
    }

    /** Tạo phản hồi 201 khi tạo mới tài nguyên thành công. */
    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(201, message, data, null, null);
    }

    /** Tạo phản hồi lỗi, luôn gắn kèm timestamp để tiện truy vết. */
    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(status, message, null, LocalDateTime.now(), null);
    }

    /** Tạo phản hồi lỗi validation kèm chi tiết lỗi theo từng trường. */
    public static <T> ApiResponse<T> error(int status, String message, Map<String, String> errors) {
        return new ApiResponse<>(status, message, null, LocalDateTime.now(), errors);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}
