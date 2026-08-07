package org.example.librarymanagement.infrastructure.config;

import org.example.librarymanagement.application.auth.ProfileService;
import org.example.librarymanagement.application.manage.UserManagementService;
import org.example.librarymanagement.application.reader.ReaderManagementService;
import org.example.librarymanagement.port.inbound.auth.ProfileUseCase;
import org.example.librarymanagement.port.inbound.reader.ReaderManagementUseCase;
import org.example.librarymanagement.port.inbound.user.ManageUserUseCase;
import org.example.librarymanagement.port.outbound.borrow.CheckActiveReaderBorrowPort;
import org.example.librarymanagement.port.outbound.reader.CardNumberGeneratorPort;
import org.example.librarymanagement.port.outbound.reader.ReaderRepositoryPort;
import org.example.librarymanagement.port.outbound.user.EncodePasswordPort;
import org.example.librarymanagement.port.outbound.user.FindUserPort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;
import org.example.librarymanagement.port.outbound.user.LoadRolePort;
import org.example.librarymanagement.port.outbound.user.SaveUserPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@EnableTransactionManagement
public class UseCaseConfig {

    @Bean
    @Transactional
    public ProfileUseCase profileUseCase(
            FindUserPort findUserPort,
            SaveUserPort saveUserPort) {
        return new ProfileService(findUserPort, saveUserPort);
    }

    @Bean
    @Transactional // Quản lý Transaction được dời ra ngoài Infrastructure
    public ManageUserUseCase manageUserUseCase(
            FindUserPort findUserPort,
            LoadRolePort loadRolePort,
            SaveUserPort saveUserPort,
            EncodePasswordPort encodePasswordPort,
            GetAuthenticatedUserPort getAuthenticatedUserPort) {

        return new UserManagementService(
                findUserPort,
                loadRolePort,
                saveUserPort,
                encodePasswordPort,
                getAuthenticatedUserPort
        );
    }

 @Bean
@Transactional
public ReaderManagementUseCase readerManagementUseCase(
        ReaderRepositoryPort readerRepositoryPort,
        GetAuthenticatedUserPort getAuthenticatedUserPort,
        CardNumberGeneratorPort cardNumberGeneratorPort,
        FindUserPort findUserPort,
        CheckActiveReaderBorrowPort checkActiveReaderBorrowPort
) {
    return new ReaderManagementService(
            readerRepositoryPort,
            getAuthenticatedUserPort,
            cardNumberGeneratorPort,
            findUserPort,
            checkActiveReaderBorrowPort
    );
}
}

