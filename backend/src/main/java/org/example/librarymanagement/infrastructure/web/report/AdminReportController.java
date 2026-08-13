package org.example.librarymanagement.infrastructure.web.report;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.port.dtos.report.BorrowReportQuery;
import org.example.librarymanagement.port.dtos.report.BorrowReportResultDto;
import org.example.librarymanagement.port.dtos.report.ReportFormat;
import org.example.librarymanagement.port.inbound.report.GetBorrowReportUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/reports/borrow-return")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    private final GetBorrowReportUseCase getBorrowReportUseCase;

    @GetMapping
    public ResponseEntity<BorrowReportResultDto> getBorrowReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) BorrowSlipStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page must be greater than or equal to 0") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be greater than 0") @Max(value = 100, message = "Size must not exceed 100") int size
    ) {
        BorrowReportQuery query = new BorrowReportQuery(startDate, endDate, status, keyword, page, size);
        BorrowReportResultDto result = getBorrowReportUseCase.getReportData(query);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/export")
    public ResponseEntity<StreamingResponseBody> exportBorrowReportCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) BorrowSlipStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "CSV") ReportFormat format
    ) {
        BorrowReportQuery query = new BorrowReportQuery(startDate, endDate, status, keyword, 0, Integer.MAX_VALUE);

        String extension = format == ReportFormat.EXCEL ? ".xlsx" : ".csv";
        String filename = "bao-cao-muon-tra-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + extension;

        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build();

        StreamingResponseBody responseBody = outputStream -> getBorrowReportUseCase.exportReportToStream(query, format, outputStream);

        MediaType mediaType = format == ReportFormat.CSV
                ? MediaType.parseMediaType("text/csv; charset=UTF-8")
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(mediaType)
                .body(responseBody);
    }
}
