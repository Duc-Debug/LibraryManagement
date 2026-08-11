package org.example.librarymanagement.infrastructure.web.borrow;

import java.time.LocalDateTime;
import java.util.List;

import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.infrastructure.web.exception.GlobalExceptionHandler;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipFilterQuery;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipResponseDto;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.inbound.borrow.BorrowSlipsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class BorrowSlipManagementControllerTest {

    @Mock
    private BorrowSlipsUseCase borrowSlipsUseCase;

    @Mock
    private org.example.librarymanagement.port.inbound.borrow.CalculateFineUseCase calculateFineUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        BorrowSlipManagementController controller = new BorrowSlipManagementController(borrowSlipsUseCase, calculateFineUseCase);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private BorrowSlipResponseDto createSampleDto(Long id, String code, BorrowSlipStatus status) {
        return new BorrowSlipResponseDto(
                id,
                code,
                10L,
                "CARD-001",
                "Nguyễn Văn A",
                1L,
                "Thủ thư B",
                LocalDateTime.of(2026, 8, 10, 10, 0),
                LocalDateTime.of(2026, 8, 24, 10, 0),
                status,
                "Ghi chú",
                2,
                LocalDateTime.of(2026, 8, 10, 10, 0)
        );
    }

    @Test
    @DisplayName("GET /api/librarians/borrow-slips: Trả về HTTP 200 kèm danh sách phân trang")
    void getBorrowSlips_Success() throws Exception {
        // Arrange
        BorrowSlipResponseDto dto = createSampleDto(1L, "PM20260810-001", BorrowSlipStatus.BORROWING);
        PageResult<BorrowSlipResponseDto> mockResult = new PageResult<>(List.of(dto), 0, 10, 1L, 1);

        when(borrowSlipsUseCase.getBorrowSlips(any(BorrowSlipFilterQuery.class))).thenReturn(mockResult);

        // Act & Assert
        mockMvc.perform(get("/api/librarians/borrow-slips")
                        .param("page", "0")
                        .param("size", "10")
                        .param("status", "BORROWING")
                        .param("keyword", "PM2026")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].borrowCode").value("PM20260810-001"))
                .andExpect(jsonPath("$.content[0].readerName").value("Nguyễn Văn A"))
                .andExpect(jsonPath("$.content[0].status").value("BORROWING"))
                .andExpect(jsonPath("$.content[0].totalBooks").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(borrowSlipsUseCase).getBorrowSlips(new BorrowSlipFilterQuery(0, 10, BorrowSlipStatus.BORROWING, "PM2026"));
    }

    @Test
    @DisplayName("GET /api/librarians/borrow-slips: Dùng giá trị mặc định khi không truyền tham số")
    void getBorrowSlips_DefaultParams() throws Exception {
        // Arrange
        PageResult<BorrowSlipResponseDto> mockResult = new PageResult<>(List.of(), 0, 10, 0L, 0);
        when(borrowSlipsUseCase.getBorrowSlips(any(BorrowSlipFilterQuery.class))).thenReturn(mockResult);

        // Act & Assert
        mockMvc.perform(get("/api/librarians/borrow-slips")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));

        verify(borrowSlipsUseCase).getBorrowSlips(new BorrowSlipFilterQuery(0, 10, null, null));
    }

    @Test
    @DisplayName("GET /api/librarians/borrow-slips/{id}/fine-preview: Trả về HTTP 200 kèm kết quả tính phạt")
    void previewFine_Success() throws Exception {
        // Arrange
        org.example.librarymanagement.port.dtos.borrow.FineCalculationResponseDto fineDto =
                new org.example.librarymanagement.port.dtos.borrow.FineCalculationResponseDto(
                        1L,
                        "PM20260810-001",
                        10L,
                        "CARD-001",
                        "Nguyễn Văn A",
                        LocalDateTime.of(2026, 8, 1, 10, 0),
                        LocalDateTime.of(2026, 8, 15, 10, 0),
                        LocalDateTime.of(2026, 8, 18, 10, 0),
                        3L,
                        2,
                        java.math.BigDecimal.valueOf(5000),
                        java.math.BigDecimal.valueOf(30000),
                        java.math.BigDecimal.valueOf(30000),
                        true,
                        "[Trả quá hạn] Quá hạn 3 ngày (2 cuốn sách)"
                );

        when(calculateFineUseCase.calculateBorrowSlipFine(any(Long.class), any())).thenReturn(fineDto);

        // Act & Assert
        mockMvc.perform(get("/api/librarians/borrow-slips/1/fine-preview")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.borrowSlipId").value(1))
                .andExpect(jsonPath("$.overdueDays").value(3))
                .andExpect(jsonPath("$.totalFineAmount").value(30000))
                .andExpect(jsonPath("$.isOverdue").value(true));

        verify(calculateFineUseCase).calculateBorrowSlipFine(1L, null);
    }
}