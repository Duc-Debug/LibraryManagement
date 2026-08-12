package org.example.librarymanagement.port.outbound.report;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

import org.example.librarymanagement.port.dtos.report.BorrowReportItemDto;
import org.example.librarymanagement.port.dtos.report.BorrowReportSummaryDto;
import org.example.librarymanagement.port.dtos.report.ReportFormat;

public interface ReportExporterPort {

    void exportToStream(
            ReportFormat format,
            LocalDate startDate,
            LocalDate endDate,
            BorrowReportSummaryDto summary,
            List<BorrowReportItemDto> items,
            OutputStream outputStream
    );

    byte[] exportToCsv(
            LocalDate startDate,
            LocalDate endDate,
            BorrowReportSummaryDto summary,
            List<BorrowReportItemDto> items
    );
}
