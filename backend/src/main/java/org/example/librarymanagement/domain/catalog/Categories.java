package org.example.librarymanagement.domain.catalog;

import java.time.LocalDateTime;
import java.util.UUID;

import org.example.librarymanagement.domain.shared.exceptions.DomainException;
public class Categories {
private UUID id;
private String name;
private String description;
private boolean isActive;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;

public Categories(UUID id, String name, String description, boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
    if(name == null || name.isEmpty()) {
        throw new DomainException("Name cannot be null or empty");
    }
    if(description == null || description.isEmpty()) {
        throw new DomainException("Description cannot be null or empty");
    }

    this.id = id;
    this.name = name;
    this.description = description;
    this.isActive = isActive;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
}
public UUID getId() {
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
