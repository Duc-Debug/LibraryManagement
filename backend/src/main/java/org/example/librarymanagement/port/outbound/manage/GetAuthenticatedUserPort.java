package org.example.librarymanagement.port.outbound.manage;

import org.example.librarymanagement.domain.entity.User;

public interface GetAuthenticatedUserPort {

    User getCurrentUser();
}
