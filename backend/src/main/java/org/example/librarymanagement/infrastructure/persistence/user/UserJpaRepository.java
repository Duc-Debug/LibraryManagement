package org.example.librarymanagement.infrastructure.persistence.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository
        extends JpaRepository<UserJpaEntity, Long> {

    @EntityGraph(attributePaths = "roles")
    Optional<UserJpaEntity> findByUsername(String username);
}