package org.example.librarymanagement.port.dtos.report;

import org.example.librarymanagement.port.dtos.common.PageResult;

public record BorrowReportResultDto(
        BorrowReportSummaryDto summary,
        PageResult<BorrowReportItemDto> items
) {
}
