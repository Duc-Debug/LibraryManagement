package org.example.librarymanagement.port.outbound.auth;

import org.example.librarymanagement.domain.entity.User;

public interface SaveUserPort {
    void save(User user);
}
