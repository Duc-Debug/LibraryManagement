package org.example.librarymanagement.infrastructure.persistence.reader;

import java.util.Optional;
import java.util.UUID;

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
    public Readers save(Readers reader) {
        ReaderJpaEntity entity = readerPersistenceMapper.toJpaEntity(reader);
        ReaderJpaEntity savedEntity = readerJpaRepository.save(entity);
        return readerPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Readers> findById(Long id) {
        return readerJpaRepository.findById(id)
                .map(readerPersistenceMapper::toDomain);
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
        return readerJpaRepository.findAll().stream()
                .map(readerPersistenceMapper::toDomain)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public java.util.List<Readers> findByCreatedByUserId(Long createdByUserId) {
        return readerJpaRepository.findByCreatedByUserId(createdByUserId).stream()
                .map(readerPersistenceMapper::toDomain)
                .collect(java.util.stream.Collectors.toList());
    }
}
