package org.example.librarymanagement.infrastructure.persistence.user;
import java.util.Optional;

import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.port.outbound.auth.LoadUserPort;
import org.example.librarymanagement.port.outbound.auth.SaveUserPort;
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
    public void save(User user) {
       if (user.getId() != null) {
            // Luồng UPDATE: Tìm Managed Entity từ DB rồi update các trường thay đổi
            userJpaRepository.findById(user.getId()).ifPresent(existingEntity -> {
                userPersistenceMapper.updateJpaEntity(user, existingEntity);
                // Vì nằm trong Transaction, Hibernate sẽ tự động Dirty Check và gọi SQL UPDATE
                userJpaRepository.save(existingEntity); 
            });
        } else {
            // Luồng CREATE: Tạo mới hoàn toàn
            UserJpaEntity newEntity = userPersistenceMapper.toJpaEntity(user);
            userJpaRepository.save(newEntity);
        }
    }

}