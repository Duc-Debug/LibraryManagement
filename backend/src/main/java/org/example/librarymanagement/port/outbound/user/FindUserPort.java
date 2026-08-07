package org.example.librarymanagement.port.outbound.user;

import java.util.List;
import java.util.Optional;

import org.example.librarymanagement.domain.entity.User;

public interface FindUserPort {

    Optional<User> findById(Long id);

    List<User> findByRoleName(String roleName);

    boolean existsByUsername(String username);
}
