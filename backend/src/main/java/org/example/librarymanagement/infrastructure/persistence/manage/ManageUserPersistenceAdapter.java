package org.example.librarymanagement.infrastructure.persistence.manage;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.infrastructure.persistence.user.RoleJpaEntity;
import org.example.librarymanagement.infrastructure.persistence.user.UserJpaEntity;
import org.example.librarymanagement.infrastructure.persistence.user.UserJpaRepository;
import org.example.librarymanagement.infrastructure.persistence.user.UserPersistenceMapper;
import org.example.librarymanagement.port.outbound.manage.FindUserPort;
import org.example.librarymanagement.port.outbound.manage.SaveUserPort;
import org.springframework.stereotype.Component;

@Component
public class ManageUserPersistenceAdapter implements FindUserPort, SaveUserPort {

    private final UserJpaRepository userJpaRepository;

    public ManageUserPersistenceAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    // ==========================================
    // Thực thi FindUserPort
    // ==========================================

    @Override
    public List<User> findByRoleName(String roleName) {
        return userJpaRepository.findByRoles_Name(roleName)
                .stream()
                .map(UserPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id)
                .map(UserPersistenceMapper::toDomain);
    }

    // ==========================================
    // Thực thi SaveUserPort
    // ==========================================

    @Override
    public User save(User user) {
        // 1. Map Domain Model sang JPA Entity
        UserJpaEntity entityToSave = toEntity(user);
        
        // 2. Lưu xuống Database thông qua Spring Data JPA
        UserJpaEntity savedEntity = userJpaRepository.save(entityToSave);
        
        // 3. Map ngược JPA Entity trả về sang Domain Model
        return UserPersistenceMapper.toDomain(savedEntity);
    }

    // ==========================================
    // Helper Methods (Mapping)
    // ==========================================

    private UserJpaEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        Set<RoleJpaEntity> roleEntities = user.getRoles() != null 
                ? user.getRoles().stream().map(this::toRoleEntity).collect(Collectors.toSet()) 
                : null;

        return new UserJpaEntity(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(), 
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.isEnabled(),
                user.getPasswordChangedAt(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                roleEntities
        );
    }

    private RoleJpaEntity toRoleEntity(Role role) {
        if (role == null) {
            return null;
        }
        return new RoleJpaEntity(
                role.getId(),
                role.getName(),
                role.getDescription()
        );
    }
}