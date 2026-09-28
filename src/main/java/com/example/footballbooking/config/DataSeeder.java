package com.example.footballbooking.config;

import com.example.footballbooking.entity.Field;
import com.example.footballbooking.entity.FieldType;
import com.example.footballbooking.entity.User;
import com.example.footballbooking.enums.ActiveStatus;
import com.example.footballbooking.enums.Role;
import com.example.footballbooking.enums.UserStatus;
import com.example.footballbooking.repository.FieldRepository;
import com.example.footballbooking.repository.FieldTypeRepository;
import com.example.footballbooking.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Khởi tạo dữ liệu mẫu cho môi trường phát triển (chỉ chạy với profile "dev").
 *
 * <p>Vì API đăng ký chỉ tạo được tài khoản USER, seeder này tạo sẵn ADMIN và OWNER
 * để hệ thống dùng được ngay. Toàn bộ thao tác đều idempotent (không tạo trùng).</p>
 */
@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final FieldTypeRepository fieldTypeRepository;
    private final FieldRepository fieldRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      FieldTypeRepository fieldTypeRepository,
                      FieldRepository fieldRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.fieldTypeRepository = fieldTypeRepository;
        this.fieldRepository = fieldRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedFieldTypes();
        User admin = seedUser("admin@footballbooking.com", "Quản trị viên", "Admin@123", Role.ADMIN);
        User owner = seedUser("owner@footballbooking.com", "Chủ sân demo", "Owner@123", Role.OWNER);
        seedUser("user@footballbooking.com", "Người dùng demo", "User@123", Role.USER);
        seedSampleField(owner);
        log.info("Đã khởi tạo dữ liệu mẫu (profile dev). Tài khoản admin: {}", admin.getEmail());
    }

    private void seedFieldTypes() {
        createTypeIfAbsent("Sân 5 người", "Sân bóng đá mini 5 người");
        createTypeIfAbsent("Sân 7 người", "Sân bóng đá 7 người");
        createTypeIfAbsent("Sân 11 người", "Sân bóng đá 11 người tiêu chuẩn");
    }

    private void createTypeIfAbsent(String name, String description) {
        if (fieldTypeRepository.findByName(name).isEmpty()) {
            FieldType type = new FieldType();
            type.setName(name);
            type.setDescription(description);
            type.setStatus(ActiveStatus.ACTIVE);
            fieldTypeRepository.save(type);
        }
    }

    private User seedUser(String email, String fullName, String rawPassword, Role role) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setEmail(email);
            user.setFullName(fullName);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setPhone("0900000000");
            user.setRole(role);
            user.setStatus(UserStatus.ACTIVE);
            return userRepository.save(user);
        });
    }

    private void seedSampleField(User owner) {
        // Chỉ tạo sân mẫu khi hệ thống chưa có sân nào
        if (fieldRepository.count() > 0) {
            return;
        }
        List<FieldType> types = fieldTypeRepository.findAll();
        if (types.isEmpty()) {
            return;
        }
        Field field = new Field();
        field.setOwner(owner);
        field.setFieldType(types.get(0));
        field.setName("Sân bóng Ngôi Sao");
        field.setAddress("123 Nguyễn Trãi, Hà Nội");
        field.setDescription("Sân cỏ nhân tạo, có mái che và đèn chiếu sáng");
        field.setPricePerHour(new BigDecimal("300000"));
        field.setStatus(ActiveStatus.ACTIVE);
        fieldRepository.save(field);
    }
}
