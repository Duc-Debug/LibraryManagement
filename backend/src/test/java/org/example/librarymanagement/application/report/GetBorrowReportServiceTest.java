package org.example.librarymanagement.application.report;

import java.time.LocalDate;
import java.util.List;

import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.shared.AccessDeniedException;
import org.example.librarymanagement.domain.policies.AuthorizationAccessPolicy;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.dtos.report.BorrowReportQuery;
import org.example.librarymanagement.port.dtos.report.BorrowReportResultDto;
import org.example.librarymanagement.port.dtos.report.BorrowReportSummaryDto;
import org.example.librarymanagement.port.outbound.report.LoadBorrowReportPort;
import org.example.librarymanagement.port.outbound.report.ReportExporterPort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetBorrowReportServiceTest {

    private LoadBorrowReportPort loadBorrowReportPort;
    private ReportExporterPort reportExporterPort;
    private GetAuthenticatedUserPort getAuthenticatedUserPort;
    private GetBorrowReportService service;

    @BeforeEach
    void setUp() {
        loadBorrowReportPort = mock(LoadBorrowReportPort.class);
        reportExporterPort = mock(ReportExporterPort.class);
        getAuthenticatedUserPort = mock(GetAuthenticatedUserPort.class);
        service = new GetBorrowReportService(loadBorrowReportPort, reportExporterPort, getAuthenticatedUserPort);
    }

    @Test
    @DisplayName("Should successfully return report data when user has ADMIN role")
    void getReportData_AsAdmin_ShouldSucceed() {
        User adminUser = new User(1L, "admin", "pass", "Admin Name", "admin@email.com", "0123456789", true, null, null, null, java.util.Set.of(new Role(1L, AuthorizationAccessPolicy.ROLE_ADMIN, "Admin")));
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(adminUser);

        when(loadBorrowReportPort.findReportItems(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(PageResult.of(List.of(), 0, 10, 0));
        when(loadBorrowReportPort.calculateSummary(any(), any(), any(), any()))
                .thenReturn(new BorrowReportSummaryDto(0, 0, 0, 0, 0, java.math.BigDecimal.ZERO));

        BorrowReportQuery query = new BorrowReportQuery(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 10), null, null, 0, 10);
        BorrowReportResultDto result = service.getReportData(query);

        assertNotNull(result);
        assertNotNull(result.summary());
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when user is LIBRARIAN (not ADMIN)")
    void getReportData_AsLibrarian_ShouldThrowAccessDenied() {
        User librarianUser = new User(2L, "librarian", "pass", "Lib Name", "lib@email.com", "0123456789", true, null, null, null, java.util.Set.of(new Role(2L, AuthorizationAccessPolicy.ROLE_LIBRARIAN, "Librarian")));
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(librarianUser);

        BorrowReportQuery query = new BorrowReportQuery(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 10), null, null, 0, 10);

        assertThrows(AccessDeniedException.class, () -> service.getReportData(query));
    }
}
