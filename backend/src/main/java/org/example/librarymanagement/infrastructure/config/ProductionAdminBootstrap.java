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

@Component
@Profile("prod")
@RequiredArgsConstructor
public class ProductionAdminBootstrap implements CommandLineRunner {

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("===> ProductionAdminBootstrap started...");

        RoleJpaEntity adminRole =
                findOrCreateRole("ADMIN", "Quản trị viên hệ thống");

        RoleJpaEntity librarianRole =
                findOrCreateRole("LIBRARIAN", "Thủ thư");

        seedAdminUser(adminRole, librarianRole);

        System.out.println("===> ProductionAdminBootstrap completed.");
    }

    private RoleJpaEntity findOrCreateRole(
            String roleName,
            String description
    ) {
        return roleJpaRepository.findByName(roleName)
                .orElseGet(() -> {
                    System.out.println("===> Creating production role: " + roleName);

                    return roleJpaRepository.save(
                            new RoleJpaEntity(
                                    null,
                                    roleName,
                                    description
                            )
                    );
                });
    }

    private void seedAdminUser(
            RoleJpaEntity adminRole,
            RoleJpaEntity librarianRole
    ) {
        String username = requireEnv("ADMIN_USERNAME");
        String password = requireEnv("ADMIN_PASSWORD");
        String fullName = requireEnv("ADMIN_FULL_NAME");
        String email = requireEnv("ADMIN_EMAIL");
        String phone = requireEnv("ADMIN_PHONE");

        if (userJpaRepository.existsByUsername(username)) {
            System.out.println(
                    "===> Production admin '" + username
                            + "' already exists. Skipping."
            );
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        UserJpaEntity adminUser = new UserJpaEntity(
                null,
                username,
                passwordEncoder.encode(password),
                fullName,
                email,
                phone,
                true,
                now,
                now,
                now,
                Set.of(adminRole, librarianRole)
        );

        userJpaRepository.save(adminUser);

        System.out.println(
                "===> Production admin created successfully: "
                        + username
        );
    }

    private String requireEnv(String name) {
        String value = System.getenv(name);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Required environment variable is missing: " + name
            );
        }

        return value;
    }
}