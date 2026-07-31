package org.example.librarymanagement.domain.entity;

import lombok.Builder;
import lombok.Getter;
import org.example.librarymanagement.domain.exceptions.DomainException;

import java.util.Objects;

@Getter
@Builder
public class Role {

    private final Long id;
    private final String name;
    private final String description;

    public Role(Long id, String name, String description) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Role name must not be blank");
        }

        this.id = id;
        this.name = name.trim().toUpperCase();
        this.description = description == null ? null : description.trim();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Role other)) {
            return false;
        }

        if (id != null && other.id != null) {
            return Objects.equals(id, other.id);
        }

        return name.equalsIgnoreCase(other.name);
    }

    @Override
    public int hashCode() {
        return id != null
                ? Objects.hash(id)
                : Objects.hash(name.toUpperCase());
    }
}