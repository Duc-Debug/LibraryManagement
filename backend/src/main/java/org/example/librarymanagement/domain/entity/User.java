package org.example.librarymanagement.domain.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.librarymanagement.domain.exceptions.DomainException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Dành riêng cho ORM/JPA / Persistence Framework
public class User {

    private Long id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String phone;
    private boolean enabled;
    private LocalDateTime passwordChangedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private final Set<Role> roles = new LinkedHashSet<>();

    private User(Long id, String username, String passwordHash, String fullName, String email, String phone,
            boolean enabled, LocalDateTime passwordChangedAt, LocalDateTime createdAt, LocalDateTime updatedAt,
            Set<Role> roles) {
        this.id = id;
        this.username = requireNotBlank(username, "Username must not be blank");
        this.passwordHash = requireNotBlank(passwordHash, "Password hash must not be blank");
        this.fullName = requireNotBlank(fullName, "Full name must not be blank");
        this.email = normalizeNullable(email);
        this.phone = normalizeNullable(phone);
        this.enabled = enabled;
        this.passwordChangedAt = passwordChangedAt;
        this.createdAt = Objects.requireNonNull(createdAt, "Created time must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated time must not be null");

        if (roles != null) {
            this.roles.addAll(roles);
        }
    }

    /**
     * Factory Method dùng cho Use Case: Tạo/Đăng ký người dùng mới
     */
    public static User createNew(
            String username,
            String passwordHash,
            String fullName,
            String email,
            String phone) {
        LocalDateTime now = LocalDateTime.now();
        return new User(
                null,
                username,
                passwordHash,
                fullName,
                email,
                phone,
                true,
                null,
                now,
                now,
                null);
    }

    /**
     * Factory Method dùng cho Persistence Layer: Khôi phục Entity từ Database
     */
    public static User restore(
            Long id,
            String username,
            String passwordHash,
            String fullName,
            String email,
            String phone,
            boolean enabled,
            LocalDateTime passwordChangedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Set<Role> roles) {
        if (id == null) {
            throw new DomainException("User id must not be null when restoring");
        }
        return new User(
                id,
                username,
                passwordHash,
                fullName,
                email,
                phone,
                enabled,
                passwordChangedAt,
                createdAt,
                updatedAt,
                roles);
    }

    // --- Domain Business Methods ---

    public void ensureCanLogin() {
        if (!enabled) {
            throw new DomainException("User account is disabled");
        }
    }

    public void activate() {
        if (!enabled) {
            enabled = true;
            touch();
        }
    }

    public void deactivate() {
        if (enabled) {
            enabled = false;
            touch();
        }
    }

    public void changePassword(String newPasswordHash) {
        String validatedPasswordHash = requireNotBlank(
                newPasswordHash,
                "New password hash must not be blank");

        if (validatedPasswordHash.equals(passwordHash)) {
            throw new DomainException("New password must be different from current password");
        }

        passwordHash = validatedPasswordHash;
        passwordChangedAt = LocalDateTime.now();
        touch();
    }

    public void addRole(Role role) {
        if (role == null) {
            throw new DomainException("Role must not be null");
        }

        if (roles.add(role)) {
            touch();
        }
    }

    public void removeRole(Role role) {
        if (role != null && roles.remove(role)) {
            touch();
        }
    }

    public boolean hasRole(String roleName) {
        if (isBlank(roleName)) {
            return false;
        }

        return roles.stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(roleName.trim()));
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    // --- Helper Validation Methods ---

    private void touch() {
        updatedAt = LocalDateTime.now();
    }

    private static String requireNotBlank(String value, String errorMessage) {
        if (isBlank(value)) {
            throw new DomainException(errorMessage);
        }
        return value.trim();
    }

    private static String normalizeNullable(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}