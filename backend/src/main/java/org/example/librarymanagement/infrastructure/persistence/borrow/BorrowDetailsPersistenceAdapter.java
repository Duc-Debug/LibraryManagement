package org.example.librarymanagement.infrastructure.persistence.borrow;

import java.util.List;
import java.util.Objects;

import org.example.librarymanagement.domain.entity.BorrowDetails;
import org.example.librarymanagement.port.outbound.borrow.BorrowDetailsRepositoryPort;
import org.springframework.stereotype.Repository;

@Repository
public class BorrowDetailsPersistenceAdapter
        implements BorrowDetailsRepositoryPort {

    private final BorrowDetailsJpaRepository borrowDetailsJpaRepository;
    private final BorrowDetailsPersistenceMapper borrowDetailsPersistenceMapper;

    public BorrowDetailsPersistenceAdapter(
            BorrowDetailsJpaRepository borrowDetailsJpaRepository,
            BorrowDetailsPersistenceMapper borrowDetailsPersistenceMapper
    ) {
        this.borrowDetailsJpaRepository =
                Objects.requireNonNull(
                        borrowDetailsJpaRepository,
                        "BorrowDetailsJpaRepository must not be null"
                );

        this.borrowDetailsPersistenceMapper =
                Objects.requireNonNull(
                        borrowDetailsPersistenceMapper,
                        "BorrowDetailsPersistenceMapper must not be null"
                );
    }

    @Override
public List<BorrowDetails> findByBorrowSlipIdForUpdate(
        Long borrowSlipId
) {
    return borrowDetailsJpaRepository
            .findByBorrowSlipIdForUpdate(borrowSlipId)
            .stream()
            .map(borrowDetailsPersistenceMapper::toDomain)
            .toList();
}

    @Override
    public List<BorrowDetails> saveAll(
            List<BorrowDetails> borrowDetails
    ) {
        List<BorrowDetailsJpaEntity> entities =
                borrowDetails.stream()
                        .map(this::prepareEntityForSave)
                        .toList();

        return borrowDetailsJpaRepository
                .saveAll(entities)
                .stream()
                .map(borrowDetailsPersistenceMapper::toDomain)
                .toList();
    }

    private BorrowDetailsJpaEntity prepareEntityForSave(
            BorrowDetails domain
    ) {
        if (domain.getId() == null) {
            return borrowDetailsPersistenceMapper.toJpaEntity(domain);
        }

        BorrowDetailsJpaEntity entity =
                borrowDetailsJpaRepository
                        .findById(domain.getId())
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Borrow detail not found with ID: "
                                                + domain.getId()
                                )
                        );

        borrowDetailsPersistenceMapper.updateJpaEntity(
                domain,
                entity
        );

        return entity;
    }
}