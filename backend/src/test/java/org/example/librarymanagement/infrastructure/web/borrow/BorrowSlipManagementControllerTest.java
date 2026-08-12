package org.example.librarymanagement.infrastructure.web.borrow;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.domain.exceptions.reader.ReaderNotFoundException;
import org.example.librarymanagement.infrastructure.web.exception.GlobalExceptionHandler;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipFilterQuery;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipResponseDto;
import org.example.librarymanagement.port.dtos.borrow.FineCalculationResponseDto;
import org.example.librarymanagement.port.dtos.borrow.OverdueSlipSummaryDto;
import org.example.librarymanagement.port.dtos.borrow.ReaderBorrowEligibilityDto;
import org.example.librarymanagement.port.dtos.borrow.ReturnBorrowSlipResponseDto;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.inbound.borrow.BorrowSlipsUseCase;
import org.example.librarymanagement.port.inbound.borrow.CalculateFineUseCase;
import org.example.librarymanagement.port.inbound.borrow.CheckBorrowEligibilityUseCase;
import org.example.librarymanagement.port.inbound.borrow.ReturnBorrowSlipUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class BorrowSlipManagementControllerTest {

    @Mock
    private BorrowSlipsUseCase borrowSlipsUseCase;

    @Mock
    private CalculateFineUseCase calculateFineUseCase;

    @Mock
    private ReturnBorrowSlipUseCase returnBorrowSlipUseCase;

    @Mock
    private CheckBorrowEligibilityUseCase checkBorrowEligibilityUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        BorrowSlipManagementController controller =
                new BorrowSlipManagementController(
                        borrowSlipsUseCase,
                        calculateFineUseCase,
                        returnBorrowSlipUseCase,
                        checkBorrowEligibilityUseCase
                );

        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private BorrowSlipResponseDto createSampleDto(
            Long id,
            String code,
            BorrowSlipStatus status
    ) {
        return new BorrowSlipResponseDto(
                id,
                code,
                10L,
                "CARD-001",
                "Nguyen Van A",
                1L,
                "Thu thu B",
                LocalDateTime.of(2026, 8, 10, 10, 0),
                LocalDateTime.of(2026, 8, 24, 10, 0),
                status,
                "Ghi chu",
                2,
                LocalDateTime.of(2026, 8, 10, 10, 0)
        );
    }

    @Test
    @DisplayName("GET /api/librarians/borrow-slips returns paged borrow slips")
    void getBorrowSlips_Success() throws Exception {
        BorrowSlipResponseDto dto =
                createSampleDto(
                        1L,
                        "PM20260810-001",
                        BorrowSlipStatus.BORROWING
                );

        PageResult<BorrowSlipResponseDto> mockResult =
                new PageResult<>(
                        List.of(dto),
                        0,
                        10,
                        1L,
                        1
                );

        when(
                borrowSlipsUseCase.getBorrowSlips(
                        any(BorrowSlipFilterQuery.class)
                )
        ).thenReturn(mockResult);

        mockMvc.perform(
                        get("/api/librarians/borrow-slips")
                                .param("page", "0")
                                .param("size", "10")
                                .param("status", "BORROWING")
                                .param("keyword", "PM2026")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(
                        jsonPath("$.content[0].borrowCode")
                                .value("PM20260810-001")
                )
                .andExpect(
                        jsonPath("$.content[0].readerName")
                                .value("Nguyen Van A")
                )
                .andExpect(
                        jsonPath("$.content[0].status")
                                .value("BORROWING")
                )
                .andExpect(
                        jsonPath("$.content[0].totalBooks")
                                .value(2)
                )
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(1)
                );

        verify(borrowSlipsUseCase)
                .getBorrowSlips(
                        new BorrowSlipFilterQuery(
                                0,
                                10,
                                BorrowSlipStatus.BORROWING,
                                "PM2026"
                        )
                );
    }

    @Test
    @DisplayName("GET /api/librarians/borrow-slips uses default params")
    void getBorrowSlips_DefaultParams() throws Exception {
        PageResult<BorrowSlipResponseDto> mockResult =
                new PageResult<>(
                        List.of(),
                        0,
                        10,
                        0L,
                        0
                );

        when(
                borrowSlipsUseCase.getBorrowSlips(
                        any(BorrowSlipFilterQuery.class)
                )
        ).thenReturn(mockResult);

        mockMvc.perform(
                        get("/api/librarians/borrow-slips")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));

        verify(borrowSlipsUseCase)
                .getBorrowSlips(
                        new BorrowSlipFilterQuery(
                                0,
                                10,
                                null,
                                null
                        )
                );
    }

    @Test
    @DisplayName("GET /api/librarians/borrow-slips/{id}/fine-preview returns fine preview")
    void previewFine_Success() throws Exception {
        FineCalculationResponseDto fineDto =
                new FineCalculationResponseDto(
                        1L,
                        "PM20260810-001",
                        10L,
                        "CARD-001",
                        "Nguyen Van A",
                        LocalDateTime.of(2026, 8, 1, 10, 0),
                        LocalDateTime.of(2026, 8, 15, 10, 0),
                        LocalDateTime.of(2026, 8, 18, 10, 0),
                        3L,
                        2,
                        BigDecimal.valueOf(5000),
                        BigDecimal.valueOf(30000),
                        BigDecimal.valueOf(30000),
                        true,
                        "Qua han 3 ngay"
                );

        when(
                calculateFineUseCase.calculateBorrowSlipFine(
                        any(Long.class),
                        any()
                )
        ).thenReturn(fineDto);

        mockMvc.perform(
                        get("/api/librarians/borrow-slips/1/fine-preview")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.borrowSlipId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.overdueDays")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$.totalFineAmount")
                                .value(30000)
                )
                .andExpect(
                        jsonPath("$.isOverdue")
                                .value(true)
                );

        verify(calculateFineUseCase)
                .calculateBorrowSlipFine(
                        1L,
                        null
                );
    }

    @Test
    @DisplayName("POST /api/librarians/borrow-slips/{id}/return returns success response")
    void returnBorrowSlip_Success() throws Exception {
        LocalDateTime returnedAt =
                LocalDateTime.of(
                        2026,
                        8,
                        12,
                        10,
                        0
                );

        ReturnBorrowSlipResponseDto response =
                new ReturnBorrowSlipResponseDto(
                        1L,
                        BorrowSlipStatus.RETURNED,
                        2,
                        returnedAt,
                        BigDecimal.ZERO
                );

        when(
                returnBorrowSlipUseCase.returnBorrowSlip(
                        anyLong()
                )
        ).thenReturn(response);

        mockMvc.perform(
                        post("/api/librarians/borrow-slips/1/return")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.borrowSlipId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("RETURNED")
                )
                .andExpect(
                        jsonPath("$.returnedBooks")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$.totalFineAmount")
                                .value(0)
                );

        verify(returnBorrowSlipUseCase)
                .returnBorrowSlip(1L);
    }

    @Test
    @DisplayName("GET /api/librarians/borrow-slips/eligibility/{readerId} returns eligible reader")
    void checkEligibility_EligibleSuccess() throws Exception {
        ReaderBorrowEligibilityDto dto = ReaderBorrowEligibilityDto.eligible(
                10L,
                "CARD-001",
                "Nguyen Van A",
                2,
                5,
                3
        );

        when(checkBorrowEligibilityUseCase.checkEligibility(10L))
                .thenReturn(dto);

        mockMvc.perform(
                        get("/api/librarians/borrow-slips/eligibility/10")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.readerId").value(10))
                .andExpect(jsonPath("$.cardNumber").value("CARD-001"))
                .andExpect(jsonPath("$.readerName").value("Nguyen Van A"))
                .andExpect(jsonPath("$.currentBorrowingCount").value(2))
                .andExpect(jsonPath("$.maxBorrowLimit").value(5))
                .andExpect(jsonPath("$.remainingBorrowLimit").value(3))
                .andExpect(jsonPath("$.hasOverdueBooks").value(false))
                .andExpect(jsonPath("$.eligible").value(true));

        verify(checkBorrowEligibilityUseCase)
                .checkEligibility(10L);
    }

    @Test
    @DisplayName("GET /api/librarians/borrow-slips/eligibility/{readerId} returns overdue rejection")
    void checkEligibility_BlockedDueToOverdue() throws Exception {
        OverdueSlipSummaryDto overdueSlip = new OverdueSlipSummaryDto(
                1L,
                "PM-2026-001",
                LocalDateTime.now().minusDays(3),
                3L,
                2
        );
        ReaderBorrowEligibilityDto dto = ReaderBorrowEligibilityDto.rejected(
                10L,
                "CARD-001",
                "Nguyen Van A",
                2,
                5,
                3,
                true,
                2,
                List.of(overdueSlip),
                "Doc gia dang co 2 cuon sach qua han chua hoan tra."
        );

        when(checkBorrowEligibilityUseCase.checkEligibility(10L))
                .thenReturn(dto);

        mockMvc.perform(
                        get("/api/librarians/borrow-slips/eligibility/10")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible").value(false))
                .andExpect(jsonPath("$.hasOverdueBooks").value(true))
                .andExpect(jsonPath("$.overdueBooksCount").value(2))
                .andExpect(jsonPath("$.overdueSlips[0].borrowCode").value("PM-2026-001"))
                .andExpect(
                        jsonPath("$.rejectionReason")
                                .value("Doc gia dang co 2 cuon sach qua han chua hoan tra.")
                );

        verify(checkBorrowEligibilityUseCase)
                .checkEligibility(10L);
    }

    @Test
    @DisplayName("GET /api/librarians/borrow-slips/eligibility/{readerId} returns 404 when reader is missing")
    void checkEligibility_ReaderNotFound() throws Exception {
        when(checkBorrowEligibilityUseCase.checkEligibility(999L))
                .thenThrow(ReaderNotFoundException.withId(999L));

        mockMvc.perform(
                        get("/api/librarians/borrow-slips/eligibility/999")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("READER_NOT_FOUND"));
    }
}
