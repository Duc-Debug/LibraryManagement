package org.example.librarymanagement.infrastructure.persistence.reader;

import org.example.librarymanagement.domain.entity.Readers;
import org.springframework.stereotype.Component;

@Component
public class ReaderPersistenceMapper {

    public Readers toDomain(ReaderJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Readers(
                entity.getId(),
                entity.getCardNumber(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhoneNumber(),
                entity.getAddress(),
                entity.getCardStatus(),
                entity.getCardIssuedAt(),
                entity.getCardExpiryAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.isActive(),
                entity.getCreatedByUserId()
        );
    }

    /**
     * Chuyển từ Domain Entity sang JPA Entity (DB)
     */
    public ReaderJpaEntity toJpaEntity(Readers domain) {
        if (domain == null) {
            return null;
        }

        return new ReaderJpaEntity(
                domain.getId(),
                domain.getCardNumber(),
                domain.getName(),
                domain.getEmail(),
                domain.getPhoneNumber(),
                domain.getAddress(),
                domain.getCardStatus(),
                domain.getCardIssuedAt(),
                domain.getCardExpiryAt(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.isActive(),
                domain.getCreatedByUserId()
        );
    }
}
