package org.example.librarymanagement.domain.entity;

import java.time.LocalDateTime;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class Categories {

    private Long id;
    private String name;
    private String description;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Categories(Long id, String name, String description, boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (name == null || name.isEmpty()) {
            throw new DomainException("Name cannot be null or empty");
        }
        if (description == null || description.isEmpty()) {
            throw new DomainException("Description cannot be null or empty");
        }

        this.id = id;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
