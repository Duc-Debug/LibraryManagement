package org.example.librarymanagement.port.outbound.auth;
import org.example.librarymanagement.domain.entity.*;

import java.util.Optional;

public interface LoadUserPort {

    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
}