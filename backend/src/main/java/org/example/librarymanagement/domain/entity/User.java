package org.example.librarymanagement.domain.entity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.example.librarymanagement.domain.exceptions.DomainException;

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

    // 1. Static Factory Method dùng khi Tạo Mới User
    public static User create(
            String username,
            String passwordHash,
            String fullName,
            String email,
            String phone) {
        return new User(
                null,
                username,
                passwordHash,
                fullName,
                email,
                phone,
                true,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                Collections.emptySet());
    }

    // 2. Full-Args Constructor dùng khi Reconstitute từ Database
    public User(
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
        this.id = id;
        this.username = requireNotBlank(username, "Username must not be blank");
        this.passwordHash = requireNotBlank(passwordHash, "Password hash must not be blank");
        this.fullName = requireNotBlank(fullName, "Full name must not be blank");
        this.email = normalizeNullable(email);
        this.phone = normalizeNullable(phone);
        this.enabled = enabled;
        this.passwordChangedAt = passwordChangedAt;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();

        if (roles != null) {
            this.roles.addAll(roles);
        }
    }

    // ==================== DOMAIN BUSINESS BEHAVIORS ====================

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

        this.passwordHash = validatedPasswordHash;
        this.passwordChangedAt = LocalDateTime.now();
        touch();
    }

    public void updateProfile(String fullName, String email, String phone) {
        this.fullName = requireNotBlank(fullName, "Full name must not be blank");
        this.email = normalizeNullable(email);
        this.phone = normalizeNullable(phone);
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

    // ==================== HELPER VALIDATIONS ====================

    private void touch() {
        this.updatedAt = LocalDateTime.now();
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

    // ==================== GETTERS ONLY (NO PUBLIC SETTERS) ====================

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public LocalDateTime getPasswordChangedAt() {
        return passwordChangedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(id, user.id) || Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : Objects.hash(username);
    }
}