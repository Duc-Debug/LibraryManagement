package org.example.librarymanagement.port.outbound.user;
import java.util.Optional;

import org.example.librarymanagement.domain.entity.User;

public interface LoadUserPort {

    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
}
