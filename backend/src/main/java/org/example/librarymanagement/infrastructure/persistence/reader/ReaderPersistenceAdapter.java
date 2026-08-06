package org.example.librarymanagement.infrastructure.persistence.reader;

import java.util.Optional;

import org.example.librarymanagement.domain.entity.Readers;
import org.example.librarymanagement.port.outbound.reader.ReaderRepositoryPort;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReaderPersistenceAdapter implements ReaderRepositoryPort {

    private final ReaderJpaRepository readerJpaRepository;
    private final ReaderPersistenceMapper readerPersistenceMapper;

    @Override
public Optional<Readers> findById(Long readerId) {
    return readerJpaRepository.findById(readerId)
            .map(readerPersistenceMapper::toDomain);
}

@Override
public Readers save(Readers reader) {
    ReaderJpaEntity entity =
            readerPersistenceMapper.toJpaEntity(reader);

    ReaderJpaEntity savedEntity =
            readerJpaRepository.save(entity);

    return readerPersistenceMapper.toDomain(savedEntity);
}
    @Override
    public Optional<Readers> findByCardNumber(String cardNumber) {
        return readerJpaRepository.findByCardNumber(cardNumber)
                .map(readerPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByCardNumber(String cardNumber) {
        return readerJpaRepository.existsByCardNumber(cardNumber);
    }

    @Override
    public boolean existsByEmail(String email) {
        return readerJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return readerJpaRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public java.util.List<Readers> findAll() {
        return readerJpaRepository.findByIsActiveTrue().stream()
                .map(readerPersistenceMapper::toDomain)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public java.util.List<Readers> findByCreatedByUserId(Long createdByUserId) {
        return readerJpaRepository.findByCreatedByUserIdAndIsActiveTrue(createdByUserId).stream()
                .map(readerPersistenceMapper::toDomain)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public org.example.librarymanagement.port.dtos.common.PageResult<Readers> findAll(int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<ReaderJpaEntity> jpaPage = readerJpaRepository.findByIsActiveTrue(pageable);

        java.util.List<Readers> content = jpaPage.getContent().stream()
                .map(readerPersistenceMapper::toDomain)
                .collect(java.util.stream.Collectors.toList());

        return org.example.librarymanagement.port.dtos.common.PageResult.of(
                content,
                jpaPage.getNumber(),
                jpaPage.getSize(),
                jpaPage.getTotalElements()
        );
    }

    @Override
    public org.example.librarymanagement.port.dtos.common.PageResult<Readers> findByCreatedByUserId(Long createdByUserId, int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<ReaderJpaEntity> jpaPage = readerJpaRepository.findByCreatedByUserIdAndIsActiveTrue(createdByUserId, pageable);

        java.util.List<Readers> content = jpaPage.getContent().stream()
                .map(readerPersistenceMapper::toDomain)
                .collect(java.util.stream.Collectors.toList());

        return org.example.librarymanagement.port.dtos.common.PageResult.of(
                content,
                jpaPage.getNumber(),
                jpaPage.getSize(),
                jpaPage.getTotalElements()
        );
    }
 @Override
public boolean existsByEmailAndIdNot(
        String email,
        Long excludedReaderId
) {
    return readerJpaRepository
            .existsByEmailIgnoreCaseAndIdNot(
                    email,
                    excludedReaderId
            );
}

@Override
public boolean existsByPhoneNumberAndIdNot(
        String phoneNumber,
        Long excludedReaderId
) {
    return readerJpaRepository
            .existsByPhoneNumberAndIdNot(
                    phoneNumber,
                    excludedReaderId
            );
}
}
