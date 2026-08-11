package org.example.librarymanagement.infrastructure.config;

import org.example.librarymanagement.application.borrow.CalculateFineService;
import org.example.librarymanagement.application.borrow.GetBorrowSlipsService;
import org.example.librarymanagement.infrastructure.transaction.borrow.TransactionalBorrowSlipsUseCase;
import org.example.librarymanagement.infrastructure.transaction.borrow.TransactionalCalculateFineUseCase;
import org.example.librarymanagement.port.inbound.borrow.BorrowSlipsUseCase;
import org.example.librarymanagement.port.inbound.borrow.CalculateFineUseCase;
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
        GetBorrowSlipsService service = new GetBorrowSlipsService(loadBorrowSlipPort, getAuthenticatedUserPort);
        return new TransactionalBorrowSlipsUseCase(service);
    }

    @Bean
    public CalculateFineUseCase calculateFineUseCase(
            LoadBorrowSlipPort loadBorrowSlipPort,
            GetAuthenticatedUserPort getAuthenticatedUserPort
    ) {
        CalculateFineService service = new CalculateFineService(loadBorrowSlipPort, getAuthenticatedUserPort);
        return new TransactionalCalculateFineUseCase(service);
    }
}