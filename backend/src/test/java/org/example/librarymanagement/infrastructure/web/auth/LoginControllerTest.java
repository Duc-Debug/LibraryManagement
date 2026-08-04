package org.example.librarymanagement.infrastructure.web.auth;

import java.util.Set;

import org.example.librarymanagement.application.auth.InvalidCredentialsException;
import org.example.librarymanagement.infrastructure.web.exception.GlobalExceptionHandler;
import org.example.librarymanagement.port.dtos.auth.LoginCommand;
import org.example.librarymanagement.port.dtos.auth.LoginResult;
import org.example.librarymanagement.port.inbound.auth.LoginUseCase;
import org.example.librarymanagement.port.inbound.auth.LogoutCommand;
import org.example.librarymanagement.port.inbound.auth.LogoutUseCase;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class LoginControllerTest {

    private LoginUseCase loginUseCase;
    private MockMvc mockMvc;
    private LogoutUseCase logoutUseCase;

    @BeforeEach
    void setUp() {
        loginUseCase = mock(LoginUseCase.class);
    logoutUseCase = mock(LogoutUseCase.class);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(loginUseCase, logoutUseCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void validRequestReturnsLoginResult() throws Exception {
        when(loginUseCase.login(new LoginCommand("alice", "secret")))
                .thenReturn(new LoginResult(
                        1L,
                        "alice",
                        "Alice Reader",
                        Set.of("ADMIN"),
                        "jwt-token"
                ));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "alice",
                                  "password": "secret"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.fullName").value("Alice Reader"))
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(content().string(not(containsString("password"))))
                .andExpect(content().string(not(containsString("passwordHash"))));

        verify(loginUseCase).login(new LoginCommand("alice", "secret"));
    }

    @Test
    void blankUsernameReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": " ",
                                  "password": "secret"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void blankPasswordReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "alice",
                                  "password": " "
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidCredentialsReturnsUnauthorized() throws Exception {
        when(loginUseCase.login(any()))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "alice",
                                  "password": "wrong"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }
    @Test
void missingAuthorizationHeaderReturnsBadRequest() throws Exception {
    mockMvc.perform(post("/api/auth/logout"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_AUTHORIZATION_HEADER"));
}

@Test
void nonBearerAuthorizationHeaderReturnsBadRequest() throws Exception {
    mockMvc.perform(post("/api/auth/logout")
                    .header("Authorization", "Basic abc"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_AUTHORIZATION_HEADER"));
}

@Test
void emptyBearerTokenReturnsBadRequest() throws Exception {
    mockMvc.perform(post("/api/auth/logout")
                    .header("Authorization", "Bearer "))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_AUTHORIZATION_HEADER"));
}

@Test
void validBearerHeaderCallsLogoutUseCaseAndReturnsNoContent() throws Exception {
    mockMvc.perform(post("/api/auth/logout")
                    .header("Authorization", "Bearer raw-jwt"))
            .andExpect(status().isNoContent());

    verify(logoutUseCase).logout(new LogoutCommand("raw-jwt"));
}
}
