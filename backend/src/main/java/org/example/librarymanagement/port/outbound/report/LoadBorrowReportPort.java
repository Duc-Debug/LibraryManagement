package org.example.librarymanagement.port.outbound.report;

import java.time.LocalDateTime;
import java.util.List;

import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.dtos.report.BorrowReportItemDto;
import org.example.librarymanagement.port.dtos.report.BorrowReportSummaryDto;

public interface LoadBorrowReportPort {

    PageResult<BorrowReportItemDto> findReportItems(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            BorrowSlipStatus status,
            String keyword,
            int page,
            int size
    );

    List<BorrowReportItemDto> findAllReportItems(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            BorrowSlipStatus status,
            String keyword
    );

    BorrowReportSummaryDto calculateSummary(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            BorrowSlipStatus status,
            String keyword
    );
}
