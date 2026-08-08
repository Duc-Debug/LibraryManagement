package org.example.librarymanagement.infrastructure.persistence.user;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.port.outbound.user.FindUserPort;
import org.example.librarymanagement.port.outbound.user.LoadUserPort;
import org.example.librarymanagement.port.outbound.user.SaveUserPort;
import org.example.librarymanagement.port.outbound.user.UserRepositoryPort;
import org.springframework.stereotype.Component;

/**
 * Unified Secondary Persistence Adapter for User Domain Entity
 * Implements UserRepositoryPort, LoadUserPort, FindUserPort, and SaveUserPort
 */
@Component
public class UserPersistenceAdapter implements UserRepositoryPort, LoadUserPort, FindUserPort, SaveUserPort {

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper userPersistenceMapper;

    public UserPersistenceAdapter(
            UserJpaRepository userJpaRepository,
            UserPersistenceMapper userPersistenceMapper
    ) {
        this.userJpaRepository = Objects.requireNonNull(userJpaRepository, "UserJpaRepository must not be null");
        this.userPersistenceMapper = Objects.requireNonNull(userPersistenceMapper, "UserPersistenceMapper must not be null");
    }

    // ==========================================
    // Implement UserRepositoryPort, LoadUserPort & FindUserPort
    // ==========================================

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository
                .findByUsername(username)
                .map(userPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository
                .findById(id)
                .map(userPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public List<User> findByRoleName(String roleName) {
        return userJpaRepository.findByRoles_Name(roleName)
                .stream()
                .map(userPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    // ==========================================
    // Implement SaveUserPort
    // ==========================================

    @Override
    public User save(User user) {
        if (user == null) {
            return null;
        }

        UserJpaEntity entityToSave;
        if (user.getId() != null) {
            // Luồng UPDATE: Tìm Managed Entity từ DB để Hibernate Dirty Check & update các trường thay đổi
            entityToSave = userJpaRepository.findById(user.getId())
                    .orElseGet(() -> userPersistenceMapper.toJpaEntity(user));
            userPersistenceMapper.updateJpaEntity(user, entityToSave);
        } else {
            // Luồng CREATE: Tạo mới hoàn toàn
            entityToSave = userPersistenceMapper.toJpaEntity(user);
        }

        UserJpaEntity savedEntity = userJpaRepository.save(entityToSave);
        return userPersistenceMapper.toDomain(savedEntity);
    }
}