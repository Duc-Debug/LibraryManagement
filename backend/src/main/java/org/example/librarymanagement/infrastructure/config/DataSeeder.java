package org.example.librarymanagement.infrastructure.config;

import java.time.LocalDateTime;
import java.util.Set;

import org.example.librarymanagement.infrastructure.persistence.user.RoleJpaEntity;
import org.example.librarymanagement.infrastructure.persistence.user.RoleJpaRepository;
import org.example.librarymanagement.infrastructure.persistence.user.UserJpaEntity;
import org.example.librarymanagement.infrastructure.persistence.user.UserJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.JdbcTemplate;

@Component
@Profile({ "local", "dev" })
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("===> Kích hoạt DataSeeder...");

        // Xử lý nới lỏng/dọn dẹp các cột thừa do ddl-auto=update từng sinh ra trong bảng readers
        try {
            jdbcTemplate.execute("ALTER TABLE readers MODIFY COLUMN card_number VARCHAR(50) NULL");
        } catch (Exception ignored) {}

        try {
            jdbcTemplate.execute("ALTER TABLE readers MODIFY COLUMN name VARCHAR(100) NULL");
        } catch (Exception ignored) {}

        try {
            jdbcTemplate.execute("ALTER TABLE readers ADD COLUMN created_by_user_id BIGINT NULL");
        } catch (Exception ignored) {}

        // 1. Seed hoặc tìm các Role bắt buộc
        RoleJpaEntity adminRole = findOrCreateRole("ADMIN", "Quản trị viên hệ thống");
        RoleJpaEntity librarianRole = findOrCreateRole("LIBRARIAN", "Thủ thư");

        // 2. Seed Admin User mặc định nếu chưa tồn tại
        seedDefaultUser("test1", "123456", "Giap Duc", "duc@gmail.com", "12345678", Set.of(adminRole, librarianRole));
    }

    private RoleJpaEntity findOrCreateRole(String roleName, String description) {
        return roleJpaRepository.findByName(roleName)
                .orElseGet(() -> {
                    System.out.println("===> Seed thêm Role: " + roleName);
                    return roleJpaRepository.save(new RoleJpaEntity(null, roleName, description));
                });
    }

    private void seedDefaultUser(String username, String rawPassword, String fullName, String email, String phone,
            Set<RoleJpaEntity> roles) {
        if (!userJpaRepository.existsByUsername(username)) {
            UserJpaEntity defaultUser = new UserJpaEntity(
                    null,
                    username,
                    passwordEncoder.encode(rawPassword),
                    fullName,
                    email,
                    phone,
                    true,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    roles);
            userJpaRepository.save(defaultUser);

            System.out.println("===> Khởi tạo thành công User mẫu!");
            System.out.println("     - Username: " + username);
            System.out.println("     - Password gốc: " + rawPassword);
        } else {
            System.out.println("===> User '" + username + "' đã tồn tại. Bỏ qua khởi tạo user mẫu.");
        }
    }
}
