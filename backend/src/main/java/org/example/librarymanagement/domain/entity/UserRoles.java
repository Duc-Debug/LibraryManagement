package org.example.librarymanagement.domain.entity;
import java.util.UUID;
import java.time.LocalDateTime;
import org.example.librarymanagement.domain.exceptions.DomainException;
public class UserRoles {
   private UUID user_id;
    private UUID role_id;
    private LocalDateTime assigned_at;

    public UserRoles() {}
    public UserRoles(UUID user_id, UUID role_id, LocalDateTime assigned_at) {
        this.user_id = user_id;
        this.role_id = role_id;
        this.assigned_at = assigned_at;
    }

    public UUID getUserId() {
        return user_id;
    }
    public void setUserId(UUID user_id) {
        if(user_id == null) {
            throw new DomainException("User ID cannot be null");
        }
        this.user_id = user_id;
    }

    public UUID getRoleId() {
        return role_id;
    }
    public void setRoleId(UUID role_id) {
        if(role_id == null) {
            throw new DomainException("Role ID cannot be null");
        }
        this.role_id = role_id;
    }

    public LocalDateTime getAssignedAt() {
        return assigned_at;
    }
    public void setAssignedAt(LocalDateTime assigned_at) {
        if(assigned_at == null) {
            throw new DomainException("Assigned at cannot be null");
        }
        this.assigned_at = assigned_at;
    }

}
