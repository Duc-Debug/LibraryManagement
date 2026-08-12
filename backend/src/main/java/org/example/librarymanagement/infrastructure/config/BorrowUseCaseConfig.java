package org.example.librarymanagement.infrastructure.config;

import org.example.librarymanagement.application.borrow.CalculateFineService;
import org.example.librarymanagement.application.borrow.CheckBorrowEligibilityService;
import org.example.librarymanagement.application.borrow.GetBorrowSlipsService;
import org.example.librarymanagement.application.borrow.ReturnBorrowSlipService;
import org.example.librarymanagement.infrastructure.transaction.borrow.TransactionalBorrowSlipsUseCase;
import org.example.librarymanagement.infrastructure.transaction.borrow.TransactionalCalculateFineUseCase;
import org.example.librarymanagement.infrastructure.transaction.borrow.TransactionalReturnBorrowSlipUseCase;
import org.example.librarymanagement.port.inbound.borrow.BorrowSlipsUseCase;
import org.example.librarymanagement.port.inbound.borrow.CalculateFineUseCase;
import org.example.librarymanagement.port.inbound.borrow.ReturnBorrowSlipUseCase;
import org.example.librarymanagement.port.outbound.book.BookRepositoryPort;
import org.example.librarymanagement.port.outbound.borrow.BorrowDetailsRepositoryPort;
import org.example.librarymanagement.port.outbound.borrow.LoadBorrowSlipPort;
import org.example.librarymanagement.port.outbound.borrow.SaveBorrowSlipPort;
import org.example.librarymanagement.infrastructure.transaction.borrow.TransactionalCheckBorrowEligibilityUseCase;
import org.example.librarymanagement.port.inbound.borrow.BorrowSlipsUseCase;
import org.example.librarymanagement.port.inbound.borrow.CalculateFineUseCase;
import org.example.librarymanagement.port.inbound.borrow.CheckBorrowEligibilityUseCase;
import org.example.librarymanagement.port.outbound.borrow.LoadBorrowSlipPort;
import org.example.librarymanagement.port.outbound.borrow.LoadReaderBorrowStatusPort;
import org.example.librarymanagement.port.outbound.reader.ReaderRepositoryPort;
import org.example.librarymanagement.port.outbound.setting.LoadSystemSettingPort;
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
  @Bean
public ReturnBorrowSlipUseCase returnBorrowSlipUseCase(
        LoadBorrowSlipPort loadBorrowSlipPort,
        SaveBorrowSlipPort saveBorrowSlipPort,
        BorrowDetailsRepositoryPort borrowDetailsRepositoryPort,
        BookRepositoryPort bookRepositoryPort,
        GetAuthenticatedUserPort getAuthenticatedUserPort
) {

    ReturnBorrowSlipUseCase service =
            new ReturnBorrowSlipService(
                    loadBorrowSlipPort,
                    saveBorrowSlipPort,
                    borrowDetailsRepositoryPort,
                    bookRepositoryPort,
                    getAuthenticatedUserPort
            );

    return new TransactionalReturnBorrowSlipUseCase(service);
}

    @Bean
    public CheckBorrowEligibilityUseCase checkBorrowEligibilityUseCase(
            ReaderRepositoryPort readerRepositoryPort,
            LoadReaderBorrowStatusPort loadReaderBorrowStatusPort,
            LoadSystemSettingPort loadSystemSettingPort,
            GetAuthenticatedUserPort getAuthenticatedUserPort
    ) {
        CheckBorrowEligibilityService service = new CheckBorrowEligibilityService(
                readerRepositoryPort,
                loadReaderBorrowStatusPort,
                loadSystemSettingPort,
                getAuthenticatedUserPort
        );
        return new TransactionalCheckBorrowEligibilityUseCase(service);
    }
}