package org.example.librarymanagement.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class Category {
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 500;

    private Long id;
    private String name;
    private String description;
    private boolean active;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Category(
            Long id,
            String name,
            String description,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.name = normalizeName(name);
        this.description = normalizeDescription(description);
        this.active = active;
        this.createdAt = requireCreatedAt(createdAt);
        this.updatedAt = requireUpdatedAt(updatedAt);

        ensureValidTimeline(
                this.createdAt,
                this.updatedAt
        );
    }

    public static Category create(
            String name,
            String description
    ) {
        LocalDateTime now = LocalDateTime.now();

        return new Category(
                null,
                name,
                description,
                true,
                now,
                now
        );
    }

    public static Category restore(
            Long id,
            String name,
            String description,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Category(
                requirePersistedId(id),
                name,
                description,
                active,
                createdAt,
                updatedAt
        );
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new DomainException(
                    "Category id has already been assigned"
            );
        }

        this.id = requirePersistedId(id);
    }

    public void rename(String newName) {
        String newValue = normalizeName(newName);

        if (name.equals(newValue)) {
            return;
        }

        name = newValue;
        markUpdated();
    }

    public void updateDescription(
            String newDescription
    ) {
        String newValue = normalizeDescription(newDescription);

        if (Objects.equals(
                description,
                newValue
        )) {
            return;
        }

        description = newValue;
        markUpdated();
    }

    public void activate() {
        if (active) {
            return;
        }

        active = true;
        markUpdated();
    }

    public void deactivate() {
        if (!active) {
            return;
        }

        active = false;
        markUpdated();
    }

    public static String normalizeName(
            String name
    ) {
        if (name == null || name.isBlank()) {
            throw new DomainException(
                    "Category name must not be blank"
            );
        }

        String normalized = name.strip()
                .replaceAll("\\s+", " ");

        if (normalized.length() > MAX_NAME_LENGTH) {
            throw new DomainException(
                    "Category name must not exceed "
                            + MAX_NAME_LENGTH
                            + " characters"
            );
        }

        return normalized;
    }

    public static String normalizeDescription(
            String description
    ) {
        if (description == null) {
            return null;
        }

        String normalized = description.strip()
                .replaceAll("\\s+", " ");

        if (normalized.isEmpty()) {
            return null;
        }

        if (normalized.length() > MAX_DESCRIPTION_LENGTH) {
            throw new DomainException(
                    "Category description must not exceed "
                            + MAX_DESCRIPTION_LENGTH
                            + " characters"
            );
        }

        return normalized;
    }

    private void markUpdated() {
        updatedAt = LocalDateTime.now();
    }

    private static Long requirePersistedId(
            Long id
    ) {
        if (id == null) {
            throw new DomainException(
                    "Category id must not be null"
            );
        }

        if (id <= 0) {
            throw new DomainException(
                    "Category id must be a positive number"
            );
        }

        return id;
    }

    private static LocalDateTime requireCreatedAt(
            LocalDateTime createdAt
    ) {
        if (createdAt == null) {
            throw new DomainException(
                    "Category createdAt must not be null"
            );
        }

        return createdAt;
    }

    private static LocalDateTime requireUpdatedAt(
            LocalDateTime updatedAt
    ) {
        if (updatedAt == null) {
            throw new DomainException(
                    "Category updatedAt must not be null"
            );
        }

        return updatedAt;
    }

    private static void ensureValidTimeline(
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        if (updatedAt.isBefore(createdAt)) {
            throw new DomainException(
                    "Category updatedAt must not be before createdAt"
            );
        }
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
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Category category)) {
            return false;
        }

        return id != null
                && id.equals(category.id);
    }

    @Override
    public int hashCode() {
        return id != null
                ? id.hashCode()
                : System.identityHashCode(this);
    }
}
