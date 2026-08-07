package org.example.librarymanagement.infrastructure.config;

import org.example.librarymanagement.application.auth.ChangePasswordService;
import org.example.librarymanagement.application.auth.LoginService;
import org.example.librarymanagement.application.auth.LogoutService;
import org.example.librarymanagement.port.inbound.auth.ChangePasswordUseCase;
import org.example.librarymanagement.port.inbound.auth.LoginUseCase;
import org.example.librarymanagement.port.inbound.auth.LogoutUseCase;
import org.example.librarymanagement.port.outbound.auth.AccessTokenIssuerPort;
import org.example.librarymanagement.port.outbound.auth.AccessTokenRevocationPort;
import org.example.librarymanagement.port.outbound.auth.AccessTokenVerifierPort;
import org.example.librarymanagement.port.outbound.auth.PasswordVerifierPort;
import org.example.librarymanagement.port.outbound.auth.SaveUserPort;
import org.example.librarymanagement.port.outbound.user.LoadUserPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class AuthApplicationConfig {

    @Bean
    public LoginUseCase loginUseCase(
            LoadUserPort loadUserPort,
            PasswordVerifierPort passwordVerifierPort,
            AccessTokenIssuerPort accessTokenIssuerPort
    ) {
        return new LoginService(
                loadUserPort,
                passwordVerifierPort,
                accessTokenIssuerPort
        );
    }

    @Bean
    public LogoutUseCase logoutUseCase(
            AccessTokenVerifierPort accessTokenVerifierPort,
            AccessTokenRevocationPort accessTokenRevocationPort
    ) {
        return new LogoutService(
                accessTokenVerifierPort,
                accessTokenRevocationPort
        );
    }

    @Bean
    @Transactional
    public ChangePasswordUseCase changePasswordUseCase(
            LoadUserPort loadUserPort,
            SaveUserPort saveUserPort,
            PasswordVerifierPort passwordVerifierPort
    ) {
        return new ChangePasswordService(
                loadUserPort,
                saveUserPort,
                passwordVerifierPort
        );
    }
}