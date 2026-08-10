package org.example.librarymanagement.infrastructure.config;

import org.example.librarymanagement.application.auth.ChangePasswordService;
import org.example.librarymanagement.application.auth.LoginService;
import org.example.librarymanagement.application.auth.LogoutService;
import org.example.librarymanagement.application.auth.ProfileService;
import org.example.librarymanagement.infrastructure.transaction.auth.TransactionalChangePasswordUseCase;
import org.example.librarymanagement.infrastructure.transaction.auth.TransactionalProfileUseCase;
import org.example.librarymanagement.port.inbound.auth.ChangePasswordUseCase;
import org.example.librarymanagement.port.inbound.auth.LoginUseCase;
import org.example.librarymanagement.port.inbound.auth.LogoutUseCase;
import org.example.librarymanagement.port.inbound.auth.ProfileUseCase;
import org.example.librarymanagement.port.outbound.auth.AccessTokenIssuerPort;
import org.example.librarymanagement.port.outbound.auth.AccessTokenRevocationPort;
import org.example.librarymanagement.port.outbound.auth.AccessTokenVerifierPort;
import org.example.librarymanagement.port.outbound.auth.PasswordVerifierPort;
import org.example.librarymanagement.port.outbound.user.SaveUserPort;
import org.example.librarymanagement.port.outbound.user.FindUserPort;
import org.example.librarymanagement.port.outbound.user.LoadUserPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthApplicationConfig {

        @Bean
        public LoginUseCase loginUseCase(
                        LoadUserPort loadUserPort,
                        PasswordVerifierPort passwordVerifierPort,
                        AccessTokenIssuerPort accessTokenIssuerPort) {
                return new LoginService(
                                loadUserPort,
                                passwordVerifierPort,
                                accessTokenIssuerPort);
        }

        @Bean
        public LogoutUseCase logoutUseCase(
                        AccessTokenVerifierPort accessTokenVerifierPort,
                        AccessTokenRevocationPort accessTokenRevocationPort) {
                return new LogoutService(
                                accessTokenVerifierPort,
                                accessTokenRevocationPort);
        }

        @Bean
        public ChangePasswordUseCase changePasswordUseCase(
                        LoadUserPort loadUserPort,
                        SaveUserPort saveUserPort,
                        PasswordVerifierPort passwordVerifierPort) {
                ChangePasswordService service = new ChangePasswordService(
                                loadUserPort,
                                saveUserPort,
                                passwordVerifierPort);
                return new TransactionalChangePasswordUseCase(service);
        }

        @Bean
        public ProfileUseCase profileUseCase(
                        FindUserPort findUserPort,
                        SaveUserPort saveUserPort) {
                ProfileService service = new ProfileService(
                                findUserPort,
                                saveUserPort);
                // Bọc Transactional Decorator Proxy chuẩn Hexagonal
                return new TransactionalProfileUseCase(service);
        }
}