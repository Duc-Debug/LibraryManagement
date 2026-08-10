package org.example.librarymanagement.port.outbound.user;

import java.util.List;
import java.util.Optional;

import org.example.librarymanagement.domain.entity.User;

/**
 * Outbound Port Interface for User Persistence Operations
 * Standardized single repository port for User Domain Entity
 */
public interface UserRepositoryPort {

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findByRoleName(String roleName);
}
