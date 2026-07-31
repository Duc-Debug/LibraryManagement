package org.example.librarymanagement.application.auth;

import org.example.librarymanagement.infrastructure.persistence.user.UserJpaRepository;
import org.example.librarymanagement.port.dtos.auth.ChangePasswordCommand;
import org.example.librarymanagement.port.dtos.auth.ChangePasswordResult;
import org.example.librarymanagement.port.inbound.auth.ChangePasswordUseCase;
import org.example.librarymanagement.port.outbound.auth.PasswordVerifierPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangePasswordService implements ChangePasswordUseCase {

    private final UserJpaRepository userRepositoryPort;
    private final PasswordVerifierPort passwordVerifierPort;
    @Override
    @Transactional
    public ChangePasswordResult changePassword(ChangePasswordCommand command) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changePassword'");
    }
    
}
