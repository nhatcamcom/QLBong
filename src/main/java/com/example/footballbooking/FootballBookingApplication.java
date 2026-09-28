package com.example.footballbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Điểm khởi động (entry point) của ứng dụng Spring Boot.
 *
 * <p>Đây là kiến trúc Modular Monolith: toàn bộ hệ thống nằm trong MỘT ứng dụng
 * Spring Boot duy nhất, chưa tách Microservices theo đúng định hướng học phần.</p>
 */
@SpringBootApplication
public class FootballBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FootballBookingApplication.class, args);
    }
}
