package org.example.librarymanagement.infrastructure.web.setting;

import java.time.LocalDateTime;
import java.util.List;

import org.example.librarymanagement.domain.exceptions.setting.SystemSettingNotFoundException;
import org.example.librarymanagement.domain.exceptions.shared.AccessDeniedException;
import org.example.librarymanagement.infrastructure.web.exception.GlobalExceptionHandler;
import org.example.librarymanagement.port.dtos.setting.SystemSettingResponseDto;
import org.example.librarymanagement.port.inbound.setting.ManageSystemSettingUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AdminSystemSettingControllerTest {

    @Mock
    private ManageSystemSettingUseCase manageSystemSettingUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AdminSystemSettingController controller = new AdminSystemSettingController(manageSystemSettingUseCase);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/admin/settings: Trả về HTTP 200 kèm danh sách cấu hình")
    void getAllSettings_Success() throws Exception {
        SystemSettingResponseDto dto = new SystemSettingResponseDto(
                1L, "MAX_CONCURRENT_BORROW_BOOKS", "5", "Giới hạn mượn", 1L, LocalDateTime.now()
        );
        when(manageSystemSettingUseCase.getAllSettings()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/admin/settings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].settingKey").value("MAX_CONCURRENT_BORROW_BOOKS"))
                .andExpect(jsonPath("$[0].settingValue").value("5"));

        verify(manageSystemSettingUseCase).getAllSettings();
    }

    @Test
    @DisplayName("GET /api/admin/settings/{key}: Trả về HTTP 200 kèm chi tiết cấu hình")
    void getSettingByKey_Success() throws Exception {
        SystemSettingResponseDto dto = new SystemSettingResponseDto(
                1L, "MAX_CONCURRENT_BORROW_BOOKS", "5", "Giới hạn mượn", 1L, LocalDateTime.now()
        );
        when(manageSystemSettingUseCase.getSettingByKey("MAX_CONCURRENT_BORROW_BOOKS")).thenReturn(dto);

        mockMvc.perform(get("/api/admin/settings/MAX_CONCURRENT_BORROW_BOOKS")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.settingKey").value("MAX_CONCURRENT_BORROW_BOOKS"))
                .andExpect(jsonPath("$.settingValue").value("5"));

        verify(manageSystemSettingUseCase).getSettingByKey("MAX_CONCURRENT_BORROW_BOOKS");
    }

    @Test
    @DisplayName("GET /api/admin/settings/{key}: Trả về HTTP 404 khi không tìm thấy cấu hình")
    void getSettingByKey_NotFound() throws Exception {
        when(manageSystemSettingUseCase.getSettingByKey("UNKNOWN_KEY"))
                .thenThrow(new SystemSettingNotFoundException("UNKNOWN_KEY"));

        mockMvc.perform(get("/api/admin/settings/UNKNOWN_KEY")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SYSTEM_SETTING_NOT_FOUND"));
    }

    @Test
    @DisplayName("PUT /api/admin/settings/{key}: Trả về HTTP 200 khi Admin cập nhật cấu hình thành công")
    void updateSetting_Success() throws Exception {
        SystemSettingResponseDto updated = new SystemSettingResponseDto(
                1L, "MAX_CONCURRENT_BORROW_BOOKS", "7", "Cập nhật 7 cuốn", 1L, LocalDateTime.now()
        );
        when(manageSystemSettingUseCase.updateSetting(eq("MAX_CONCURRENT_BORROW_BOOKS"), eq("7"), eq("Cập nhật 7 cuốn")))
                .thenReturn(updated);

        String jsonRequest = """
                {
                    "settingValue": "7",
                    "description": "Cập nhật 7 cuốn"
                }
                """;

        mockMvc.perform(put("/api/admin/settings/MAX_CONCURRENT_BORROW_BOOKS")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.settingKey").value("MAX_CONCURRENT_BORROW_BOOKS"))
                .andExpect(jsonPath("$.settingValue").value("7"))
                .andExpect(jsonPath("$.description").value("Cập nhật 7 cuốn"));

        verify(manageSystemSettingUseCase).updateSetting("MAX_CONCURRENT_BORROW_BOOKS", "7", "Cập nhật 7 cuốn");
    }

    @Test
    @DisplayName("PUT /api/admin/settings/{key}: Trả về HTTP 403 khi không có quyền Admin")
    void updateSetting_ForbiddenWhenNotAdmin() throws Exception {
        when(manageSystemSettingUseCase.updateSetting(anyString(), anyString(), anyString()))
                .thenThrow(new AccessDeniedException("Access denied: This feature requires Administrator (ADMIN) role."));

        String jsonRequest = """
                {
                    "settingValue": "7",
                    "description": "Cập nhật"
                }
                """;

        mockMvc.perform(put("/api/admin/settings/MAX_CONCURRENT_BORROW_BOOKS")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @DisplayName("PUT /api/admin/settings/{key}: Trả về HTTP 400 khi body thiếu settingValue")
    void updateSetting_BadRequestWhenBlankValue() throws Exception {
        String jsonRequest = """
                {
                    "settingValue": "",
                    "description": "Thiếu giá trị"
                }
                """;

        mockMvc.perform(put("/api/admin/settings/MAX_CONCURRENT_BORROW_BOOKS")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}
