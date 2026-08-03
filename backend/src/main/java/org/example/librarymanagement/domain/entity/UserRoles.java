package org.example.librarymanagement.domain.entity;
import java.util.UUID;
import java.time.LocalDateTime;
import org.example.librarymanagement.domain.exceptions.DomainException;

public class UserRoles {
   private UUID userId;
    private UUID roleId;
    private LocalDateTime assignedAt;

    public UserRoles() {}
    public UserRoles(UUID userId, UUID roleId, LocalDateTime assignedAt) {
        this.userId = userId;
        this.roleId = roleId;
        this.assignedAt = assignedAt;
    }

    public UUID getUserId() {
        return userId;
    }
    public void setUserId(UUID userId) {
        if(userId == null) {
            throw new DomainException("User ID cannot be null");
        }
        this.userId = userId;
    }

    public UUID getRoleId() {
        return roleId;
    }
    public void setRoleId(UUID roleId) {
        if(roleId == null) {
            throw new DomainException("Role ID cannot be null");
        }
        this.roleId = roleId;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }
    public void setAssignedAt(LocalDateTime assignedAt) {
        if(assignedAt == null) {
            throw new DomainException("Assigned at cannot be null");
        }
        this.assignedAt = assignedAt;
    }

}
