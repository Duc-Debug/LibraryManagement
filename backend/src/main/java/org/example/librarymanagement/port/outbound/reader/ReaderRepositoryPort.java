package org.example.librarymanagement.port.outbound.reader;

import java.util.Optional;

import org.example.librarymanagement.domain.entity.Readers;

public interface ReaderRepositoryPort {

    Readers save(Readers reader);

    Optional<Readers> findById(Long id);

    Optional<Readers> findByCardNumber(String cardNumber);

    boolean existsByCardNumber(String cardNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    java.util.List<Readers> findAll();

    org.example.librarymanagement.port.dtos.common.PageResult<Readers> findAll(int page, int size);
}
