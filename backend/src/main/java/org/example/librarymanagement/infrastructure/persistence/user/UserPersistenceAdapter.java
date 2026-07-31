package org.example.librarymanagement.infrastructure.persistence.user;

import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.port.outbound.auth.LoadUserPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserPersistenceAdapter implements LoadUserPort {

    private final UserJpaRepository userJpaRepository;

    public UserPersistenceAdapter(
            UserJpaRepository userJpaRepository
    ) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository
                .findByUsername(username)
                .map(UserPersistenceMapper::toDomain);
    }
    @Override
    public Optional<User> findByUserId(Long id){
        return userJpaRepository
            .findById(id)
            .map(UserPersistenceMapper::toDomain);
    }
}