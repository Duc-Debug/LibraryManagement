package org.example.librarymanagement.domain.entity;
import java.rmi.server.UID;
import java.time.LocalDateTime;
import org.example.librarymanagement.domain.exceptions.DomainException;
public class user_roles {
   private UID user_id;
    private UID role_id;
    private LocalDateTime assigned_at;

    public user_roles() {}
    public user_roles(UID user_id, UID role_id, LocalDateTime assigned_at) {
        this.user_id = user_id;
        this.role_id = role_id;
        this.assigned_at = assigned_at;
    }

    public UID getUser_id() {
        return user_id;
    }
    public void setUser_id(UID user_id) {
        if(user_id == null) {
            throw new DomainException("User ID cannot be null");
        }
        this.user_id = user_id;
    }

    public UID getRole_id() {
        return role_id;
    }
    public void setRole_id(UID role_id) {
        if(role_id == null) {
            throw new DomainException("Role ID cannot be null");
        }
        this.role_id = role_id;
    }

    public LocalDateTime getAssigned_at() {
        return assigned_at;
    }
    public void setAssigned_at(LocalDateTime assigned_at) {
        if(assigned_at == null) {
            throw new DomainException("Assigned at cannot be null");
        }
        this.assigned_at = assigned_at;
    }

}
