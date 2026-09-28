package com.example.footballbooking.exception;

import com.example.footballbooking.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Bộ xử lý ngoại lệ tập trung cho toàn bộ REST API.
 *
 * <p>Mục tiêu bảo mật: KHÔNG bao giờ để lộ stack trace hay thông tin nội bộ
 * ra Client. Mọi ngoại lệ đều được chuyển thành {@link ApiResponse} thống nhất
 * với đúng HTTP status code tương ứng.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 404 - Không tìm thấy tài nguyên. */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    /** 400 - Yêu cầu sai về mặt nghiệp vụ. */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    /** 409 - Xung đột với trạng thái hiện tại (trùng dữ liệu, trùng lịch...). */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    /** 401 - Chưa xác thực (chưa đăng nhập hoặc token không hợp lệ). */
    @ExceptionHandler({UnauthorizedException.class, AuthenticationException.class})
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
    }

    /**
     * 403 - Đã đăng nhập nhưng không đủ quyền.
     * Gồm cả kiểm tra quyền sở hữu tài nguyên ({@code ForbiddenException}) và
     * chặn theo vai trò của Spring Security ({@code AccessDeniedException}).
     */
    @ExceptionHandler({ForbiddenException.class, AccessDeniedException.class})
    public ResponseEntity<ApiResponse<Void>> handleForbidden(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(),
                        "Bạn không có quyền thực hiện thao tác này"));
    }

    /**
     * 400 - Lỗi validation cho dữ liệu gửi trong body (@Valid @RequestBody).
     * Gom toàn bộ lỗi theo từng trường để Frontend hiển thị chính xác.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            // Giữ lại thông báo lỗi đầu tiên cho mỗi trường để tránh nhiễu
            fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Dữ liệu không hợp lệ", fieldErrors));
    }

    /**
     * 409 - Vi phạm ràng buộc toàn vẹn dữ liệu (ví dụ: trùng unique khi hai request
     * đăng ký cùng email chạy song song). Bắt riêng để trả 409 thay vì 500.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(HttpStatus.CONFLICT.value(),
                        "Dữ liệu bị trùng hoặc vi phạm ràng buộc, vui lòng kiểm tra lại"));
    }

    /** 400 - Lỗi validation cho tham số trên URL (@RequestParam, @PathVariable). */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    /**
     * 404 - Gọi tới đường dẫn không tồn tại.
     * Bắt riêng để không bị handler tổng quát biến thành 500 (sai HTTP status).
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResource(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Không tìm thấy tài nguyên yêu cầu"));
    }

    /** 405 - Gọi endpoint đúng đường dẫn nhưng sai HTTP method. */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(HttpStatus.METHOD_NOT_ALLOWED.value(),
                        "Phương thức HTTP không được hỗ trợ cho tài nguyên này"));
    }

    /** 400 - Body JSON bị thiếu hoặc sai định dạng, không thể đọc. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Dữ liệu gửi lên không đọc được"));
    }

    /**
     * 500 - Mọi ngoại lệ chưa được lường trước.
     * Ghi log chi tiết ở phía server nhưng chỉ trả thông báo chung cho Client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex) {
        log.error("Lỗi hệ thống không mong đợi", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Đã xảy ra lỗi hệ thống, vui lòng thử lại sau"));
    }
}
