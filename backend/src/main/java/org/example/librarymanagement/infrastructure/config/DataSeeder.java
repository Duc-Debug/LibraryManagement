package org.example.librarymanagement.infrastructure.config;

import java.time.LocalDateTime;
import java.util.Set;

import org.example.librarymanagement.infrastructure.persistence.book.BookJpaEntity;
import org.example.librarymanagement.infrastructure.persistence.book.BookJpaRepository;
import org.example.librarymanagement.infrastructure.persistence.category.CategoryJpaEntity;
import org.example.librarymanagement.infrastructure.persistence.category.CategoryJpaRepository;
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

@Component
@Profile({ "local", "dev" })
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final BookJpaRepository bookJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("===> Kích hoạt DataSeeder...");

        // 1. Seed hoặc tìm các Role bắt buộc
        RoleJpaEntity adminRole = findOrCreateRole("ADMIN", "Quản trị viên hệ thống");
        RoleJpaEntity librarianRole = findOrCreateRole("LIBRARIAN", "Thủ thư");

        // 2. Seed Admin User mặc định nếu chưa tồn tại
        seedDefaultUser("test1", "123456", "Giap Duc", "duc@gmail.com", "12345678", Set.of(adminRole, librarianRole));

        // 3. Seed dữ liệu Sách mẫu nếu chưa có
        seedDefaultBooks();
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

    private void seedDefaultBooks() {
        if (bookJpaRepository.count() == 0) {
            System.out.println("===> Bắt đầu Seed dữ liệu Sách mẫu...");

            // 1. Seed 1 Danh mục mặc định nếu chưa có
            CategoryJpaEntity defaultCategory = categoryJpaRepository.findByName("Công nghệ & Phần mềm")
                    .orElseGet(() -> categoryJpaRepository.save(
                            new CategoryJpaEntity(null, "Công nghệ & Phần mềm", "Danh mục sách công nghệ")
                    ));

            Long categoryId = defaultCategory.getId();

            BookJpaEntity book1 = new BookJpaEntity(
                    null,
                    "Lập trình Java với DDD & Hexagonal Architecture",
                    "Robert C. Martin",
                    "978-0134494166",
                    "Sách hướng dẫn thiết kế phần mềm sạch theo kiến trúc Hexagonal và DDD",
                    "https://example.com/cover1.jpg",
                    "NXB Tri Thức",
                    (short) 2024,
                    "Kệ A1-01",
                    5,
                    5,
                    categoryId,
                    true,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            BookJpaEntity book2 = new BookJpaEntity(
                    null,
                    "Clean Architecture",
                    "Uncle Bob",
                    "978-0134494167",
                    "Kiến trúc phần mềm sạch dành cho lập trình viên Java",
                    "https://example.com/cover2.jpg",
                    "NXB Thống Kê",
                    (short) 2023,
                    "Kệ A1-02",
                    3,
                    3,
                    categoryId,
                    true,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            BookJpaEntity book3 = new BookJpaEntity(
                    null,
                    "Refactoring: Improving the Design of Existing Code",
                    "Martin Fowler",
                    "978-0201485677",
                    "Phương pháp tái cấu trúc mã nguồn tối ưu",
                    "https://example.com/cover3.jpg",
                    "NXB Công Nghệ",
                    (short) 2022,
                    "Kệ B2-05",
                    2,
                    2,
                    categoryId,
                    true,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            bookJpaRepository.save(book1);
            bookJpaRepository.save(book2);
            bookJpaRepository.save(book3);

            System.out.println("===> Seed thành công 3 cuốn sách mẫu vào Database!");
        } else {
            System.out.println("===> Bảng sách đã có dữ liệu. Bỏ qua khởi tạo sách mẫu.");
        }
    }
}
