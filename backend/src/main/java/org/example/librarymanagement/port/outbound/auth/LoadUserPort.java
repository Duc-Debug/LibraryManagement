package org.example.librarymanagement.port.outbound.auth;
import java.util.Optional;

import org.example.librarymanagement.domain.auth.User;

public interface LoadUserPort {

    Optional<User> findByUsername(String username);
}
