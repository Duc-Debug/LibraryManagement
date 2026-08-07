package org.example.librarymanagement.infrastructure.persistence.user;

import java.util.Optional;

import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.port.outbound.user.SaveUserPort;
import org.example.librarymanagement.port.outbound.user.LoadUserPort;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements LoadUserPort, SaveUserPort {

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper userPersistenceMapper;

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
    public User save(User user) {
        if (user == null) {
            return null;
        }
        UserJpaEntity entityToSave;
        if (user.getId() != null) {
            // Luồng UPDATE: Tìm Managed Entity từ DB để Hibernate Dirty Check & update các
            // trường thay đổi
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