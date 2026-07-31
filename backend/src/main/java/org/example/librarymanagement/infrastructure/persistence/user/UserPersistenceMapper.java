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
                roles);
    }

    private static Role toDomainRole(RoleJpaEntity entity) {
        return new Role(
                entity.getId(),
                entity.getName(),
                entity.getDescription());
    }

    public static UserJpaEntity toJpaEntity(User domain) {
        if (domain == null) {
            return null;
        }
        Set<RoleJpaEntity> roleEntities = domain.getRoles() != null
                ? domain.getRoles().stream()
                        .map(UserPersistenceMapper::toJpaEntityRole)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
                : new LinkedHashSet<>();

        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(domain.getId());
        entity.setUsername(domain.getUsername());
        entity.setPasswordHash(domain.getPasswordHash());
        entity.setFullName(domain.getFullName());
        entity.setEmail(domain.getEmail());
        entity.setPhone(domain.getPhone());
        entity.setEnabled(domain.isEnabled());
        entity.setPasswordChangedAt(domain.getPasswordChangedAt());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setRoles(roleEntities);
 
        return entity;
    }

    private static RoleJpaEntity toJpaEntityRole(Role domain){
        if(domain ==null){
            return null;
        }
        RoleJpaEntity entity = new RoleJpaEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        return entity;
    }
}