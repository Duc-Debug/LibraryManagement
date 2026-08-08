package org.example.librarymanagement.port.outbound.reader;

import java.util.Optional;

import org.example.librarymanagement.domain.entity.Reader;
import org.example.librarymanagement.port.dtos.common.PageResult;

public interface ReaderRepositoryPort {

    Reader save(Reader reader);

    Optional<Reader> findById(Long id);

    Optional<Reader> findByCardNumber(String cardNumber);

    boolean existsByCardNumber(String cardNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    PageResult<Reader> search(ReaderSearchCriteria criteria);

    java.util.List<Reader> findAll();

    java.util.List<Reader> findByCreatedByUserId(Long createdByUserId);

    PageResult<Reader> findAll(int page, int size);

    PageResult<Reader> findByCreatedByUserId(Long createdByUserId, int page, int size);
    boolean existsByEmailAndIdNot(
        String email,
        Long excludedReaderId
);

boolean existsByPhoneNumberAndIdNot(
        String phoneNumber,
        Long excludedReaderId
);
}
