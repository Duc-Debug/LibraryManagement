package org.example.librarymanagement.infrastructure.config;

import org.example.librarymanagement.application.report.GetBorrowReportService;
import org.example.librarymanagement.port.inbound.report.GetBorrowReportUseCase;
import org.example.librarymanagement.port.outbound.report.LoadBorrowReportPort;
import org.example.librarymanagement.port.outbound.report.ReportExporterPort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportUseCaseConfig {

    @Bean
    public GetBorrowReportUseCase getBorrowReportUseCase(
            LoadBorrowReportPort loadBorrowReportPort,
            ReportExporterPort reportExporterPort,
            GetAuthenticatedUserPort getAuthenticatedUserPort
    ) {
        return new GetBorrowReportService(
                loadBorrowReportPort,
                reportExporterPort,
                getAuthenticatedUserPort
        );
    }
}
