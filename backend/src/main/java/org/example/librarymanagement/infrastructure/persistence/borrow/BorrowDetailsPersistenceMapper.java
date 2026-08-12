package org.example.librarymanagement.infrastructure.persistence.borrow;

import org.example.librarymanagement.domain.entity.BorrowDetails;
import org.springframework.stereotype.Component;

@Component
public class BorrowDetailsPersistenceMapper {

    public BorrowDetails toDomain(BorrowDetailsJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new BorrowDetails(
                entity.getId(),
                entity.getBorrowSlipId(),
                entity.getBookId(),
                entity.getReturnedAt(),
                entity.getReturnedByUserId(),
                entity.getFineAmount(),
                entity.getFineReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public BorrowDetailsJpaEntity toJpaEntity(BorrowDetails domain) {
        if (domain == null) {
            return null;
        }

        return new BorrowDetailsJpaEntity(
                domain.getId(),
                domain.getBorrowSlipId(),
                domain.getBookId(),
                domain.getReturnAt(),
                domain.getReturnByUserId(),
                domain.getFineAmount(),
                domain.getFineReason(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }

    public void updateJpaEntity(
            BorrowDetails domain,
            BorrowDetailsJpaEntity entity
    ) {
        if (domain == null || entity == null) {
            return;
        }

        entity.setReturnedAt(domain.getReturnAt());
        entity.setReturnedByUserId(domain.getReturnByUserId());
        entity.setFineAmount(domain.getFineAmount());
        entity.setFineReason(domain.getFineReason());
        entity.setUpdatedAt(domain.getUpdatedAt());
    }
}