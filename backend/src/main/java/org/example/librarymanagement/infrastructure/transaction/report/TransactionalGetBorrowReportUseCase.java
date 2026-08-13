package org.example.librarymanagement.infrastructure.transaction.report;

import java.io.OutputStream;
import java.util.Objects;

import org.example.librarymanagement.port.dtos.report.BorrowReportQuery;
import org.example.librarymanagement.port.dtos.report.BorrowReportResultDto;
import org.example.librarymanagement.port.dtos.report.ReportFormat;
import org.example.librarymanagement.port.inbound.report.GetBorrowReportUseCase;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalGetBorrowReportUseCase implements GetBorrowReportUseCase {

    private final GetBorrowReportUseCase delegate;

    public TransactionalGetBorrowReportUseCase(GetBorrowReportUseCase delegate) {
        this.delegate = Objects.requireNonNull(delegate, "GetBorrowReportUseCase delegate must not be null");
    }

    @Override
    @Transactional(readOnly = true)
    public BorrowReportResultDto getReportData(BorrowReportQuery query) {
        return delegate.getReportData(query);
    }

    @Override
    @Transactional(readOnly = true)
    public void exportReportToStream(BorrowReportQuery query, ReportFormat format, OutputStream outputStream) {
        delegate.exportReportToStream(query, format, outputStream);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportReportCsv(BorrowReportQuery query) {
        return delegate.exportReportCsv(query);
    }
}
