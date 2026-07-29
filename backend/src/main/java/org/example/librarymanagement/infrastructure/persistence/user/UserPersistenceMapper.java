package org.example.librarymanagement.infrastructure.persistence.user;

import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.entity.User;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class UserPersistenceMapper {

    private UserPersistenceMapper() {
    }

    public static User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        Set<Role> roles = entity.getRoles()
                .stream()
                .map(UserPersistenceMapper::toDomainRole)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getPasswordHash(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.isEnabled(),
                entity.getPasswordChangedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                roles
        );
    }

    private static Role toDomainRole(RoleJpaEntity entity) {
        return new Role(
                entity.getId(),
                entity.getName(),
                entity.getDescription()
        );
    }
}