package org.example.librarymanagement.infrastructure.persistence.reader;

import org.example.librarymanagement.domain.entity.Readers;
import org.springframework.stereotype.Component;

@Component
public class ReaderPersistenceMapper {

    public Readers toDomain(ReaderJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return Readers.builder()
                .id(entity.getId())
                .cardNumber(entity.getCardNumber())
                .name(entity.getName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .address(entity.getAddress())
                .cardStatus(entity.getCardStatus())
                .cardIssuedAt(entity.getCardIssuedAt())
                .cardExpiryAt(entity.getCardExpiryAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .isActive(entity.isActive())
                .build();
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
                domain.isActive()
        );
    }
}
