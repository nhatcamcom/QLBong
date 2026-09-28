package com.example.footballbooking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Tạo và xác thực JSON Web Token (JWT) bằng thuật toán HMAC-SHA.
 *
 * <p>Khóa bí mật và thời hạn token được đọc từ cấu hình (biến môi trường),
 * KHÔNG hard-code trong mã nguồn.</p>
 */
@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs) {
        // Khóa HMAC-SHA256 yêu cầu tối thiểu 32 byte; secret cấu hình phải đủ dài
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /** Sinh token với subject là email và kèm claim vai trò. */
    public String generateToken(String email, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    /** Lấy email (subject) từ token; ném ngoại lệ nếu token không hợp lệ/hết hạn. */
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    /** Kiểm tra token còn hợp lệ (chữ ký đúng và chưa hết hạn) hay không. */
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
