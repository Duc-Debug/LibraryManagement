package org.example.librarymanagement.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.librarymanagement.domain.exceptions.DomainException;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    private UUID id;
    private String name;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor dùng khi tạo mới Category
    public Category(String name, String description) {
        this.name = requireNotBlank(name, "Name cannot be null or empty");
        this.description = requireNotBlank(description, "Description cannot be null or empty");
        this.active = true;

        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // // Constructor dùng khi re-constitute từ Database
    // public Category(UUID id, String name, String description, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
    //     this.id = id;
    //     this.name = requireNotBlank(name, "Name cannot be null or empty");
    //     this.description = requireNotBlank(description, "Description cannot be null or empty");
    //     this.active = active;
    //     this.createdAt = createdAt;
    //     this.updatedAt = updatedAt;
    // }

    private static String requireNotBlank(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new DomainException(errorMessage);
        }
        return value.trim();
    }
}