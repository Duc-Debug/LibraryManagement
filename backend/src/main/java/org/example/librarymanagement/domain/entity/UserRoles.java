package org.example.librarymanagement.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.librarymanagement.domain.exceptions.DomainException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
public class UserRoles {

    private UUID userId;
    private UUID roleId;
    private LocalDateTime assignedAt;

    public UserRoles(UUID userId, UUID roleId, LocalDateTime assignedAt) {
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.roleId = Objects.requireNonNull(roleId, "Role ID cannot be null");
        this.assignedAt = assignedAt != null ? assignedAt : LocalDateTime.now();
    }
}