package org.example.librarymanagement.port.outbound.user;

import org.example.librarymanagement.domain.entity.User;

public interface GetAuthenticatedUserPort {

    User getCurrentUser();
}
