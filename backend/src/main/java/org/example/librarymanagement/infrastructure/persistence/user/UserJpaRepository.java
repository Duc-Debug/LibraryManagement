package org.example.librarymanagement.infrastructure.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository
        extends JpaRepository<UserJpaEntity, Long> {
    boolean existsByUsername(String username);

    Optional<UserJpaEntity> findByUsername(String username);

    Optional<UserJpaEntity> findById(Long id);
}