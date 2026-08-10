package org.example.librarymanagement.infrastructure.persistence.borrow;

import java.util.List;
import java.util.Objects;

import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipResponseDto;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.outbound.borrow.LoadBorrowSlipPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class BorrowSlipPersistenceAdapter implements LoadBorrowSlipPort {

    private final BorrowSlipJpaRepository borrowSlipJpaRepository;

    public BorrowSlipPersistenceAdapter(BorrowSlipJpaRepository borrowSlipJpaRepository) {
        this.borrowSlipJpaRepository = Objects.requireNonNull(
                borrowSlipJpaRepository,
                "BorrowSlipJpaRepository must not be null"
        );
    }

    @Override
    public PageResult<BorrowSlipResponseDto> findBorrowSlips(
            int page,
            int size,
            BorrowSlipStatus status,
            String keyword
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "borrowed_at", "id"));
        String statusStr = status != null ? status.name() : null;

        Page<BorrowSlipSummaryProjection> jpaPage = borrowSlipJpaRepository.findBorrowSlipsWithFilter(
                statusStr,
                keyword,
                pageable
        );

        List<BorrowSlipResponseDto> content = jpaPage.getContent().stream()
                .map(this::mapToDto)
                .toList();

        return PageResult.of(
                content,
                jpaPage.getNumber(),
                jpaPage.getSize(),
                jpaPage.getTotalElements()
        );
    }

    private BorrowSlipResponseDto mapToDto(BorrowSlipSummaryProjection p) {
        BorrowSlipStatus status = null;
        if (p.getStatus() != null) {
            try {
                status = BorrowSlipStatus.valueOf(p.getStatus());
            } catch (IllegalArgumentException ignored) {
            }
        }

        return new BorrowSlipResponseDto(
                p.getId(),
                p.getBorrowCode(),
                p.getReaderId(),
                p.getReaderCardNumber(),
                p.getReaderName(),
                p.getCreatedByUserId(),
                p.getCreatedByUserName(),
                p.getBorrowedAt(),
                p.getDueAt(),
                status,
                p.getNote(),
                p.getTotalBooks() != null ? p.getTotalBooks() : 0,
                p.getCreatedAt()
        );
    }
}