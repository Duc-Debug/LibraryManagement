package org.example.librarymanagement.infrastructure.persistence.borrow;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.example.librarymanagement.domain.entity.BorrowSlip;
import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.domain.exceptions.borrow.BorrowSlipNotFoundException;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipDetailResponseDto;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipItemResponseDto;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipResponseDto;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.outbound.borrow.LoadBorrowSlipPort;
import org.example.librarymanagement.port.outbound.borrow.SaveBorrowSlipPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class BorrowSlipPersistenceAdapter
                implements LoadBorrowSlipPort, SaveBorrowSlipPort {
        private static final int DEFAULT_PAGE_SIZE = 10;
        private static final int MAX_PAGE_SIZE = 100;

        private final BorrowSlipJpaRepository borrowSlipJpaRepository;
        private final BorrowSlipPersistenceMapper borrowSlipPersistenceMapper;
        private final BorrowDetailsJpaRepository borrowDetailsJpaRepository;

        public BorrowSlipPersistenceAdapter(
                        BorrowSlipJpaRepository borrowSlipJpaRepository,
                        BorrowSlipPersistenceMapper borrowSlipPersistenceMapper,
                        BorrowDetailsJpaRepository borrowDetailsJpaRepository) {
                this.borrowSlipJpaRepository = Objects.requireNonNull(
                                borrowSlipJpaRepository,
                                "BorrowSlipJpaRepository must not be null");
                this.borrowSlipPersistenceMapper = Objects.requireNonNull(
                                borrowSlipPersistenceMapper,
                                "BorrowSlipPersistenceMapper must not be null");
                this.borrowDetailsJpaRepository = Objects.requireNonNull(
                                borrowDetailsJpaRepository,
                                "BorrowDetailsJpaRepository must not be null");
        }

        @Override
        public PageResult<BorrowSlipResponseDto> findBorrowSlips(
                        int page,
                        int size,
                        BorrowSlipStatus status,
                        String keyword) {
                int safePage = Math.max(0, page);
                int safeSize = (size <= 0) ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);

                Pageable pageable = PageRequest.of(safePage, safeSize);
                String statusStr = status != null ? status.name() : null;

                Page<BorrowSlipSummaryProjection> jpaPage = borrowSlipJpaRepository.findBorrowSlipsWithFilter(
                                statusStr,
                                keyword,
                                pageable);

                List<BorrowSlipResponseDto> content = jpaPage.getContent().stream()
                                .map(borrowSlipPersistenceMapper::toDto)
                                .toList();

                return PageResult.of(
                                content,
                                jpaPage.getNumber(),
                                jpaPage.getSize(),
                                jpaPage.getTotalElements());
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
        public Optional<BorrowSlip> findByIdForUpdate(Long id) {
                if (id == null) {
                        return Optional.empty();
                }

                return borrowSlipJpaRepository
                                .findByIdForUpdate(id)
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

        @Override
        public Optional<BorrowSlipDetailResponseDto> findSlipDetailWithItemsById(Long id) {
                if (id == null) {
                        return Optional.empty();
                }
                Optional<BorrowSlipSummaryProjection> slipOpt = borrowSlipJpaRepository.findBorrowSlipDetailById(id);
                if (slipOpt.isEmpty()) {
                        return Optional.empty();
                }
                BorrowSlipSummaryProjection slip = slipOpt.get();
                List<BorrowDetailItemProjection> itemProjections = borrowDetailsJpaRepository.findItemsByBorrowSlipId(id);
                List<BorrowSlipItemResponseDto> items = itemProjections.stream()
                                .map(p -> new BorrowSlipItemResponseDto(
                                                p.getId(),
                                                p.getBookId(),
                                                p.getBookTitle(),
                                                p.getIsbn(),
                                                p.getAuthor(),
                                                p.getCategoryName(),
                                                p.getCoverUrl(),
                                                p.getReturnedAt(),
                                                p.getFineAmount(),
                                                p.getFineReason()
                                )).toList();

                BorrowSlipDetailResponseDto dto = new BorrowSlipDetailResponseDto(
                                slip.getId(),
                                slip.getBorrowCode(),
                                slip.getReaderId(),
                                slip.getReaderCardNumber(),
                                slip.getReaderName(),
                                slip.getCreatedByUserId(),
                                slip.getCreatedByUserName(),
                                slip.getBorrowedAt(),
                                slip.getDueAt(),
                                BorrowSlipStatus.valueOf(slip.getStatus()),
                                slip.getNote(),
                                slip.getTotalBooks(),
                                slip.getCreatedAt(),
                                items
                );
                return Optional.of(dto);
        }

        @Override
        public List<BorrowSlip> findAllActivePastDue(LocalDate referenceDate) {
                if (referenceDate == null) {
                        referenceDate = LocalDate.now();
                }
                return borrowSlipJpaRepository.findAllActivePastDue(referenceDate)
                                .stream()
                                .map(borrowSlipPersistenceMapper::toDomain)
                                .toList();
        }

        @Override
        public BorrowSlip save(BorrowSlip borrowSlip) {

                Objects.requireNonNull(
                                borrowSlip,
                                "BorrowSlip must not be null");
                BorrowSlipJpaEntity entity;

                if (borrowSlip.getId() == null) {
                        entity = create(borrowSlip);
                } else {
                        entity = update(borrowSlip);
                }
                BorrowSlipJpaEntity savedEntity = borrowSlipJpaRepository.save(entity);
                return borrowSlipPersistenceMapper.toDomain(savedEntity);

        }

        @Override
        public List<BorrowSlip> saveAll(List<BorrowSlip> borrowSlips) {
                Objects.requireNonNull(borrowSlips, "BorrowSlip list must not be null");
                List<BorrowSlipJpaEntity> entities = borrowSlips.stream()
                                .map(slip -> {
                                        if (slip.getId() == null) {
                                                return borrowSlipPersistenceMapper.toJpaEntity(slip);
                                        }
                                        BorrowSlipJpaEntity entity = borrowSlipJpaRepository
                                                        .findById(slip.getId())
                                                        .orElseThrow(() -> new BorrowSlipNotFoundException(slip.getId()));
                                        borrowSlipPersistenceMapper.updateJpaEntity(slip, entity);
                                        return entity;
                                })
                                .toList();
                return borrowSlipJpaRepository.saveAll(entities)
                                .stream()
                                .map(borrowSlipPersistenceMapper::toDomain)
                                .toList();
        }

        private BorrowSlipJpaEntity create(BorrowSlip borrowSlip) {
                return borrowSlipPersistenceMapper.toJpaEntity(borrowSlip);
        }

        private BorrowSlipJpaEntity update(BorrowSlip borrowSlip) {
                BorrowSlipJpaEntity entity = borrowSlipJpaRepository
                                .findById(borrowSlip.getId())
                                .orElseThrow(() -> new BorrowSlipNotFoundException(borrowSlip.getId()));

                borrowSlipPersistenceMapper.updateJpaEntity(
                                borrowSlip,
                                entity);
                return entity;
        }
}