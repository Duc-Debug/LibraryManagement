package org.example.librarymanagement.infrastructure.persistence.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository
        extends JpaRepository<UserJpaEntity, Long> {
    boolean existsByUsername(String username);

    @EntityGraph(attributePaths = "roles")
    Optional<UserJpaEntity> findByUsername(String username);

    Optional<UserJpaEntity> findById(Long id);

    List<UserJpaEntity> findByRoles_Name(String roleName);
}
