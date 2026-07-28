package org.example.librarymanagement.domain.entity;
import java.rmi.server.UID;
import java.time.LocalDateTime;
import org.example.librarymanagement.domain.exceptions.DomainException;
public class roles {
    private UID id;
    private String name;
    private String description;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    public roles() {}

    public roles(UID id, String name, String description, LocalDateTime created_at, LocalDateTime updated_at) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public UID getId() {
        return id;
    }

    public void setId(UID id) {
        if(id == null) {
            throw new DomainException("ID cannot be null");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name == null) {
            throw new DomainException("Name cannot be null");
        }
        if(name.trim().isEmpty()) {
            throw new DomainException("Name cannot be empty");
        }
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if(description == null) {
            throw new DomainException("Description cannot be null");
        }
        if(description.trim().isEmpty()) {
            throw new DomainException("Description cannot be empty");
        }
        this.description = description;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        if(created_at == null) {
            throw new DomainException("Created date cannot be null");
        }
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        if(updated_at != null && created_at != null && updated_at.isBefore(created_at)) {
            throw new DomainException("Updated date cannot be before created date");
        }
        this.updated_at = updated_at;
    }
}
