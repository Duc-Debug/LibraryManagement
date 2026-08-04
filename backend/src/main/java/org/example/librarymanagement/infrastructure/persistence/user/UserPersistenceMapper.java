package org.example.librarymanagement.infrastructure.persistence.user;

import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.entity.User;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserPersistenceMapper {

    public User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        Set<Role> roles = entity.getRoles() != null
                ? entity.getRoles().stream()
                        .map(this::toDomainRole)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
                : new LinkedHashSet<>();

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

    private Role toDomainRole(RoleJpaEntity entity) {
        if (entity == null)
            return null;
        return new Role(
                entity.getId(),
                entity.getName(),
                entity.getDescription());
    }

    public UserJpaEntity toJpaEntity(User domain) {
        if (domain == null) {
            return null;
        }

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

        if (domain.getRoles() != null) {
            Set<RoleJpaEntity> roleEntities = domain.getRoles().stream()
                    .map(this::toJpaEntityRole)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            entity.setRoles(roleEntities);
        }

        return entity;
    }

    private RoleJpaEntity toJpaEntityRole(Role domain) {
        if (domain == null) {
            return null;
        }
        RoleJpaEntity entity = new RoleJpaEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        return entity;
    }

    // Cập nhật dữ liệu từ Domain vào Managed Entity hiện có (Dành cho UPDATE)
    public void updateJpaEntity(User domain, UserJpaEntity targetEntity) {
        if (domain == null || targetEntity == null) {
            return;
        }

        targetEntity.setPasswordHash(domain.getPasswordHash());
        targetEntity.setPasswordChangedAt(domain.getPasswordChangedAt());
        targetEntity.setFullName(domain.getFullName());
        targetEntity.setEmail(domain.getEmail());
        targetEntity.setPhone(domain.getPhone());
        targetEntity.setEnabled(domain.isEnabled());
        targetEntity.setUpdatedAt(domain.getUpdatedAt());

        // Không gán lại set targetEntity.setRoles(...) trừ khi có thay đổi role rõ ràng
    }
}