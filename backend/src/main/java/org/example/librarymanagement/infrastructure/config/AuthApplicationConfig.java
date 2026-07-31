package org.example.librarymanagement.infrastructure.config;

import org.example.librarymanagement.application.auth.LoginService;
import org.example.librarymanagement.application.auth.LogoutService;
import org.example.librarymanagement.port.inbound.auth.LoginUseCase;
import org.example.librarymanagement.port.inbound.auth.LogoutUseCase;
import org.example.librarymanagement.port.outbound.auth.AccessTokenIssuerPort;
import org.example.librarymanagement.port.outbound.auth.LoadUserPort;
import org.example.librarymanagement.port.outbound.auth.PasswordVerifierPort;
import org.example.librarymanagement.port.outbound.auth.TokenBlacklistPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthApplicationConfig {

    @Bean
    LoginUseCase loginUseCase(
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
            TokenBlacklistPort tokenBlacklistPort
    ) {
        return new LogoutService(tokenBlacklistPort);
    }
}
