package org.example.librarymanagement.infrastructure.config;

import org.example.librarymanagement.application.auth.LoginService;
import org.example.librarymanagement.port.inbound.auth.LoginUseCase;
import org.example.librarymanagement.port.outbound.auth.LoadUserPort;
import org.example.librarymanagement.port.outbound.auth.PasswordVerifierPort;
import org.example.librarymanagement.port.outbound.auth.TokenProviderPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthenticationBeanConfig {

    @Bean
    public LoginUseCase loginUseCase(
            LoadUserPort loadUserPort,
            PasswordVerifierPort passwordVerifierPort,
            TokenProviderPort tokenProviderPort
    ) {
        return new LoginService(
                loadUserPort,
                passwordVerifierPort,
                tokenProviderPort
        );
    }
}