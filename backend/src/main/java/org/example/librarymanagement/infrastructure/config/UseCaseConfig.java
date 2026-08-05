package org.example.librarymanagement.infrastructure.config;

import org.example.librarymanagement.application.manage.UserManagementService;
import org.example.librarymanagement.application.reader.ReaderManagementService;
import org.example.librarymanagement.port.inbound.manage.ManageUserUseCase;
import org.example.librarymanagement.port.inbound.reader.CreateReaderUseCase;
import org.example.librarymanagement.port.outbound.admin.EncodePasswordPort;
import org.example.librarymanagement.port.outbound.manage.FindUserPort;
import org.example.librarymanagement.port.outbound.manage.GetAuthenticatedUserPort;
import org.example.librarymanagement.port.outbound.manage.LoadRolePort;
import org.example.librarymanagement.port.outbound.manage.SaveUserPort;
import org.example.librarymanagement.port.outbound.reader.ReaderRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import org.example.librarymanagement.port.outbound.reader.CardNumberGeneratorPort;

@Configuration
@EnableTransactionManagement
public class UseCaseConfig {

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
    public CreateReaderUseCase createReaderUseCase(
            ReaderRepositoryPort readerRepositoryPort,
            GetAuthenticatedUserPort getAuthenticatedUserPort,
            CardNumberGeneratorPort cardNumberGeneratorPort,
            FindUserPort findUserPort) {
        return new ReaderManagementService(readerRepositoryPort, getAuthenticatedUserPort, cardNumberGeneratorPort, findUserPort);
    }
}
