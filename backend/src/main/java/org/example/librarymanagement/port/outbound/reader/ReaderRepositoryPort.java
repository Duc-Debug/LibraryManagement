package org.example.librarymanagement.port.outbound.reader;

import java.util.Optional;

import org.example.librarymanagement.domain.entity.Readers;
import org.example.librarymanagement.port.dtos.common.PageResult;

public interface ReaderRepositoryPort {

    Readers save(Readers reader);

    Optional<Readers> findById(Long id);

    Optional<Readers> findByCardNumber(String cardNumber);

    boolean existsByCardNumber(String cardNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    PageResult<Readers> search(ReaderSearchCriteria criteria);

    java.util.List<Readers> findAll();

    java.util.List<Readers> findByCreatedByUserId(Long createdByUserId);

    PageResult<Readers> findAll(int page, int size);

    PageResult<Readers> findByCreatedByUserId(Long createdByUserId, int page, int size);
    boolean existsByEmailAndIdNot(
        String email,
        Long excludedReaderId
);

boolean existsByPhoneNumberAndIdNot(
        String phoneNumber,
        Long excludedReaderId
);
}
