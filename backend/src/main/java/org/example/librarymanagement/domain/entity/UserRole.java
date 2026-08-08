package org.example.librarymanagement.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class UserRole {

    private final Long userId;
    private final Long roleId;
    private final LocalDateTime assignedAt;

    // 1. Static Factory Method dùng khi Tạo Mới Liên Kết User - Role
    public static UserRole create(Long userId, Long roleId) {
        return new UserRole(userId, roleId, LocalDateTime.now());
    }

    // 2. Full-Args Constructor dùng khi Reconstitute từ Database
    public UserRole(Long userId, Long roleId, LocalDateTime assignedAt) {
        if (userId == null) {
            throw new DomainException("User ID must not be null");
        }
        if (roleId == null) {
            throw new DomainException("Role ID must not be null");
        }

        this.userId = userId;
        this.roleId = roleId;
        this.assignedAt = assignedAt != null ? assignedAt : LocalDateTime.now();
    }

    // ==================== GETTERS ONLY (NO PUBLIC SETTERS) ====================

    public Long getUserId() {
        return userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserRole userRole = (UserRole) o;
        return Objects.equals(userId, userRole.userId) && Objects.equals(roleId, userRole.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleId);
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "userId=" + userId +
                ", roleId=" + roleId +
                ", assignedAt=" + assignedAt +
                '}';
    }
}