package org.example.librarymanagement.infrastructure.persistence.manage;

import java.util.Optional;

import org.example.librarymanagement.infrastructure.persistence.user.RoleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, Long> {

    Optional<RoleJpaEntity> findByName(String name);
}