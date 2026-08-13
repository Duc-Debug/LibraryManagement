package org.example.librarymanagement.application.report;

import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

import org.example.librarymanagement.application.shared.ValidationException;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.shared.UnauthenticatedException;
import org.example.librarymanagement.domain.policies.AccountLockPolicy;
import org.example.librarymanagement.domain.policies.AuthorizationAccessPolicy;
import org.example.librarymanagement.domain.policies.DateRangePolicy;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.dtos.report.BorrowReportItemDto;
import org.example.librarymanagement.port.dtos.report.BorrowReportQuery;
import org.example.librarymanagement.port.dtos.report.BorrowReportResultDto;
import org.example.librarymanagement.port.dtos.report.BorrowReportSummaryDto;
import org.example.librarymanagement.port.dtos.report.ReportFormat;
import org.example.librarymanagement.port.inbound.report.GetBorrowReportUseCase;
import org.example.librarymanagement.port.outbound.report.LoadBorrowReportPort;
import org.example.librarymanagement.port.outbound.report.ReportExporterPort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;

public class GetBorrowReportService implements GetBorrowReportUseCase {

    public static final int MAX_PAGE_SIZE = 100;

    private final LoadBorrowReportPort loadBorrowReportPort;
    private final ReportExporterPort reportExporterPort;
    private final GetAuthenticatedUserPort getAuthenticatedUserPort;

    public GetBorrowReportService(
            LoadBorrowReportPort loadBorrowReportPort,
            ReportExporterPort reportExporterPort,
            GetAuthenticatedUserPort getAuthenticatedUserPort
    ) {
        this.loadBorrowReportPort = Objects.requireNonNull(
                loadBorrowReportPort,
                "LoadBorrowReportPort must not be null"
        );
        this.reportExporterPort = Objects.requireNonNull(
                reportExporterPort,
                "ReportExporterPort must not be null"
        );
        this.getAuthenticatedUserPort = Objects.requireNonNull(
                getAuthenticatedUserPort,
                "GetAuthenticatedUserPort must not be null"
        );
    }

    @Override
    public BorrowReportResultDto getReportData(BorrowReportQuery query) {
        verifyAdminAccess();
        validateQuery(query);

        DateRangePolicy.NormalizedDateRange normalizedRange = DateRangePolicy.validateAndNormalize(
                query.startDate(),
                query.endDate()
        );

        String keyword = normalizeKeyword(query.keyword());

        PageResult<BorrowReportItemDto> items = loadBorrowReportPort.findReportItems(
                normalizedRange.startDateTime(),
                normalizedRange.endDateTime(),
                query.status(),
                keyword,
                query.page(),
                query.size()
        );

        BorrowReportSummaryDto summary = loadBorrowReportPort.calculateSummary(
                normalizedRange.startDateTime(),
                normalizedRange.endDateTime(),
                query.status(),
                keyword
        );

        return new BorrowReportResultDto(summary, items);
    }

    @Override
    public void exportReportToStream(BorrowReportQuery query, ReportFormat format, OutputStream outputStream) {
        verifyAdminAccess();
        if (query == null) {
            throw new ValidationException("Filter query must not be null");
        }
        if (outputStream == null) {
            throw new ValidationException("Output stream must not be null");
        }

        ReportFormat targetFormat = format != null ? format : ReportFormat.CSV;

        DateRangePolicy.NormalizedDateRange normalizedRange = DateRangePolicy.validateAndNormalize(
                query.startDate(),
                query.endDate()
        );

        String keyword = normalizeKeyword(query.keyword());

        List<BorrowReportItemDto> allItems = loadBorrowReportPort.findAllReportItems(
                normalizedRange.startDateTime(),
                normalizedRange.endDateTime(),
                query.status(),
                keyword
        );

        BorrowReportSummaryDto summary = loadBorrowReportPort.calculateSummary(
                normalizedRange.startDateTime(),
                normalizedRange.endDateTime(),
                query.status(),
                keyword
        );

        reportExporterPort.exportToStream(
                targetFormat,
                normalizedRange.startDateTime().toLocalDate(),
                normalizedRange.endDateTime().toLocalDate(),
                summary,
                allItems,
                outputStream
        );
    }

    @Override
    public byte[] exportReportCsv(BorrowReportQuery query) {
        verifyAdminAccess();
        if (query == null) {
            throw new ValidationException("Filter query must not be null");
        }

        DateRangePolicy.NormalizedDateRange normalizedRange = DateRangePolicy.validateAndNormalize(
                query.startDate(),
                query.endDate()
        );

        String keyword = normalizeKeyword(query.keyword());

        List<BorrowReportItemDto> allItems = loadBorrowReportPort.findAllReportItems(
                normalizedRange.startDateTime(),
                normalizedRange.endDateTime(),
                query.status(),
                keyword
        );

        BorrowReportSummaryDto summary = loadBorrowReportPort.calculateSummary(
                normalizedRange.startDateTime(),
                normalizedRange.endDateTime(),
                query.status(),
                keyword
        );

        return reportExporterPort.exportToCsv(
                normalizedRange.startDateTime().toLocalDate(),
                normalizedRange.endDateTime().toLocalDate(),
                summary,
                allItems
        );
    }

    private void verifyAdminAccess() {
        User currentUser = getAuthenticatedUserPort.getCurrentUser();
        if (currentUser == null) {
            throw new UnauthenticatedException("User is unauthenticated");
        }

        AccountLockPolicy.validateAccountActive(currentUser);
        AuthorizationAccessPolicy.validateAdminAccess(currentUser);
    }

    private void validateQuery(BorrowReportQuery query) {
        if (query == null) {
            throw new ValidationException("Filter query must not be null");
        }
        if (query.page() < 0) {
            throw new ValidationException("Page index must be greater than or equal to 0");
        }
        if (query.size() <= 0) {
            throw new ValidationException("Page size must be greater than 0");
        }
        if (query.size() > MAX_PAGE_SIZE) {
            throw new ValidationException("Page size must not exceed " + MAX_PAGE_SIZE);
        }
    }

    private String normalizeKeyword(String keyword) {
        return (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
    }
}
