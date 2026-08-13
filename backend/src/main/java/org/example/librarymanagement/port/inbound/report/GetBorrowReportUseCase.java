package org.example.librarymanagement.port.inbound.report;

import java.io.OutputStream;

import org.example.librarymanagement.port.dtos.report.BorrowReportQuery;
import org.example.librarymanagement.port.dtos.report.BorrowReportResultDto;
import org.example.librarymanagement.port.dtos.report.ReportFormat;

public interface GetBorrowReportUseCase {

    BorrowReportResultDto getReportData(BorrowReportQuery query);

    void exportReportToStream(BorrowReportQuery query, ReportFormat format, OutputStream outputStream);

    byte[] exportReportCsv(BorrowReportQuery query);
}
