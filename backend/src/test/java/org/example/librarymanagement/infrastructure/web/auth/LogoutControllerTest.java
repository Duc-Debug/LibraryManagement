package org.example.librarymanagement.infrastructure.web.auth;

import org.example.librarymanagement.infrastructure.web.GlobalExceptionHandler;
import org.example.librarymanagement.port.inbound.auth.LogoutCommand;
import org.example.librarymanagement.port.inbound.auth.LogoutResult;
import org.example.librarymanagement.port.inbound.auth.LogoutUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LogoutControllerTest {

    private LogoutUseCase logoutUseCase;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        logoutUseCase = mock(LogoutUseCase.class);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new LogoutController(logoutUseCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void validRequestReturnsLogoutResult() throws Exception {
        when(logoutUseCase.logout(new LogoutCommand("jwt-token-123")))
                .thenReturn(new LogoutResult(true, "Logout successful."));

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "token": "jwt-token-123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Logout successful."));

        verify(logoutUseCase).logout(new LogoutCommand("jwt-token-123"));
    }

    @Test
    void blankTokenReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "token": " "
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}