package org.example.librarymanagement.infrastructure.config;

import org.example.librarymanagement.application.borrow.GetBorrowSlipsService;
import org.example.librarymanagement.port.inbound.borrow.BorrowSlipsUseCase;
import org.example.librarymanagement.port.outbound.borrow.LoadBorrowSlipPort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BorrowUseCaseConfig {

    @Bean
    public BorrowSlipsUseCase borrowSlipsUseCase(
            LoadBorrowSlipPort loadBorrowSlipPort,
            GetAuthenticatedUserPort getAuthenticatedUserPort
    ) {
        return new GetBorrowSlipsService(loadBorrowSlipPort, getAuthenticatedUserPort);
    }
}