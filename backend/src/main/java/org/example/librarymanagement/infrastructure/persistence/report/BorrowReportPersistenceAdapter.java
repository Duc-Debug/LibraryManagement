package org.example.librarymanagement.infrastructure.persistence.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.dtos.report.BorrowReportItemDto;
import org.example.librarymanagement.port.dtos.report.BorrowReportSummaryDto;
import org.example.librarymanagement.port.outbound.report.LoadBorrowReportPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BorrowReportPersistenceAdapter implements LoadBorrowReportPort {

    private final BorrowReportJpaRepository repository;

    @Override
    public PageResult<BorrowReportItemDto> findReportItems(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            BorrowSlipStatus status,
            String keyword,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        String statusStr = status != null ? status.name() : null;

        Page<BorrowReportProjection> projectionPage = repository.findReportProjectionsWithFilter(
                startDateTime,
                endDateTime,
                statusStr,
                keyword,
                pageable
        );

        List<BorrowReportItemDto> dtoList = projectionPage.getContent().stream()
                .map(this::mapToItemDto)
                .toList();

        return PageResult.of(
                dtoList,
                projectionPage.getNumber(),
                projectionPage.getSize(),
                projectionPage.getTotalElements()
        );
    }

    @Override
    public List<BorrowReportItemDto> findAllReportItems(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            BorrowSlipStatus status,
            String keyword
    ) {
        String statusStr = status != null ? status.name() : null;
        List<BorrowReportProjection> projections = repository.findAllReportProjectionsWithFilter(
                startDateTime,
                endDateTime,
                statusStr,
                keyword
        );

        return projections.stream()
                .map(this::mapToItemDto)
                .toList();
    }

    @Override
    public BorrowReportSummaryDto calculateSummary(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            BorrowSlipStatus status,
            String keyword
    ) {
        String statusStr = status != null ? status.name() : null;
        BorrowReportSummaryAggregationProjection proj = repository.calculateSummaryNative(
                startDateTime,
                endDateTime,
                statusStr,
                keyword
        );

        if (proj == null) {
            return new BorrowReportSummaryDto(0, 0, 0, 0, 0, BigDecimal.ZERO);
        }

        return new BorrowReportSummaryDto(
                proj.getTotalSlips() != null ? proj.getTotalSlips() : 0L,
                proj.getTotalBooksBorrowed() != null ? proj.getTotalBooksBorrowed() : 0L,
                proj.getTotalReturned() != null ? proj.getTotalReturned() : 0L,
                proj.getTotalOverdue() != null ? proj.getTotalOverdue() : 0L,
                proj.getTotalBorrowing() != null ? proj.getTotalBorrowing() : 0L,
                proj.getTotalFineAmount() != null ? proj.getTotalFineAmount() : BigDecimal.ZERO
        );
    }

    private BorrowReportItemDto mapToItemDto(BorrowReportProjection p) {
        BorrowSlipStatus statusEnum = null;
        if (p.getStatus() != null) {
            try {
                statusEnum = BorrowSlipStatus.valueOf(p.getStatus());
            } catch (IllegalArgumentException ignored) {
            }
        }

        return new BorrowReportItemDto(
                p.getBorrowSlipId(),
                p.getBorrowCode(),
                p.getReaderCardCode(),
                p.getReaderName(),
                p.getBookTitles() != null ? p.getBookTitles() : "",
                p.getTotalBooks() != null ? p.getTotalBooks() : 0L,
                p.getBorrowedAt(),
                p.getDueAt(),
                p.getActualReturnedAt(),
                statusEnum,
                p.getFineAmount() != null ? p.getFineAmount() : BigDecimal.ZERO,
                p.getCreatedByUserName()
        );
    }
}
