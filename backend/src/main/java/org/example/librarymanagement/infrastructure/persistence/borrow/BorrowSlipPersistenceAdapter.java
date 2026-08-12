package org.example.librarymanagement.infrastructure.persistence.borrow;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.example.librarymanagement.domain.entity.BorrowSlip;
import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipResponseDto;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.outbound.borrow.LoadBorrowSlipPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class BorrowSlipPersistenceAdapter implements LoadBorrowSlipPort {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    private final BorrowSlipJpaRepository borrowSlipJpaRepository;
    private final BorrowSlipPersistenceMapper borrowSlipPersistenceMapper;

    public BorrowSlipPersistenceAdapter(
            BorrowSlipJpaRepository borrowSlipJpaRepository,
            BorrowSlipPersistenceMapper borrowSlipPersistenceMapper
    ) {
        this.borrowSlipJpaRepository = Objects.requireNonNull(
                borrowSlipJpaRepository,
                "BorrowSlipJpaRepository must not be null"
        );
        this.borrowSlipPersistenceMapper = Objects.requireNonNull(
                borrowSlipPersistenceMapper,
                "BorrowSlipPersistenceMapper must not be null"
        );
    }

    @Override
    public PageResult<BorrowSlipResponseDto> findBorrowSlips(
            int page,
            int size,
            BorrowSlipStatus status,
            String keyword
    ) {
        int safePage = Math.max(0, page);
        int safeSize = (size <= 0) ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);

        Pageable pageable = PageRequest.of(safePage, safeSize);
        String statusStr = status != null ? status.name() : null;

        Page<BorrowSlipSummaryProjection> jpaPage = borrowSlipJpaRepository.findBorrowSlipsWithFilter(
                statusStr,
                keyword,
                pageable
        );

        List<BorrowSlipResponseDto> content = jpaPage.getContent().stream()
                .map(borrowSlipPersistenceMapper::toDto)
                .toList();

        return PageResult.of(
                content,
                jpaPage.getNumber(),
                jpaPage.getSize(),
                jpaPage.getTotalElements()
        );
    }

    @Override
    public Optional<BorrowSlip> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return borrowSlipJpaRepository.findById(id)
                .map(borrowSlipPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByBorrowCode(String borrowCode) {
        if (borrowCode == null || borrowCode.isBlank()) {
            return false;
        }
        return borrowSlipJpaRepository.existsByBorrowCode(borrowCode.trim());
    }

    @Override
    public Optional<BorrowSlipResponseDto> findSlipDetailById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return borrowSlipJpaRepository.findBorrowSlipDetailById(id)
                .map(borrowSlipPersistenceMapper::toDto);
    }
}