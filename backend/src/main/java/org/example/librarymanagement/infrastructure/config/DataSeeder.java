package org.example.librarymanagement.infrastructure.config;

import java.time.LocalDateTime;
import java.util.Set;

import org.example.librarymanagement.infrastructure.persistence.user.RoleJpaEntity;
import org.example.librarymanagement.infrastructure.persistence.user.RoleJpaRepository;
import org.example.librarymanagement.infrastructure.persistence.user.UserJpaEntity;
import org.example.librarymanagement.infrastructure.persistence.user.UserJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserJpaRepository userJpaRepository, RoleJpaRepository roleJpaRepository,
            PasswordEncoder passwordEncoder) {
        this.userJpaRepository = userJpaRepository;
        this.roleJpaRepository = roleJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userJpaRepository.count() == 0) {
            System.out.println("===> DB null. Create data seed");

            RoleJpaEntity adminRole = new RoleJpaEntity(null, "ADMIN", "Quản trị viên hệ thống");
            RoleJpaEntity librarianRole = new RoleJpaEntity(null, "LIBRARIAN", "Thủ thư");
            adminRole = roleJpaRepository.save(adminRole);
            librarianRole = roleJpaRepository.save(librarianRole);

            UserJpaEntity defaultUser = new UserJpaEntity(null, "test1", passwordEncoder.encode("123456"), "Giap Duc","duc@gmail.com", "12345678", true, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),
                    Set.of(adminRole, librarianRole));
           
                    defaultUser.setRoles(Set.of(adminRole, librarianRole));
            userJpaRepository.save(defaultUser);
            System.out.println("===> Khởi tạo thành công User mẫu!");
            System.out.println("     - ID: 1");
            System.out.println("     - Username: testuser");
            System.out.println("     - Password gốc: 123456");
        } else {
            System.out.println("===> DB đã có dữ liệu. Bỏ qua Seeder.");
        }
    }
}
