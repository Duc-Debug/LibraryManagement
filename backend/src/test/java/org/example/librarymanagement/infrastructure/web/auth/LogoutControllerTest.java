package org.example.librarymanagement.infrastructure.web.auth;

import org.example.librarymanagement.infrastructure.web.GlobalExceptionHandler;
import org.example.librarymanagement.port.dtos.auth.LogoutCommand;
import org.example.librarymanagement.port.dtos.auth.LogoutResult;
import org.example.librarymanagement.port.inbound.auth.LogoutUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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

        // Khởi tạo MockMvc kiểu Standalone giống hệt LoginControllerTest
        mockMvc = MockMvcBuilders
                .standaloneSetup(new LogoutController(logoutUseCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Thành công: Đăng xuất với Header Bearer token hợp lệ")
    void logout_WithValidBearerToken_ShouldReturn200OK() throws Exception {
        String token = "valid-jwt-token";
        when(logoutUseCase.logout(new LogoutCommand(token)))
                .thenReturn(new LogoutResult(true, "Logout successful"));

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Logout successful"));

        verify(logoutUseCase).logout(new LogoutCommand(token));
    }

    @Test
    @DisplayName("Thất bại: Không truyền Header hoặc truyền sai định dạng Bearer")
    void logout_WithInvalidHeader_ShouldReturn400BadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "InvalidHeaderFormat")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(logoutUseCase, never()).logout(any());
    }

    @Test
    @DisplayName("Thất bại: UseCase xử lý thất bại (token không hợp lệ hoặc đã bị revoke)")
    void logout_WhenUseCaseReturnsFailure_ShouldReturn400BadRequest() throws Exception {
        String token = "invalid-token";
        when(logoutUseCase.logout(new LogoutCommand(token)))
                .thenReturn(new LogoutResult(false, "Token is invalid"));

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Token is invalid"));

        verify(logoutUseCase).logout(new LogoutCommand(token));
    }
}