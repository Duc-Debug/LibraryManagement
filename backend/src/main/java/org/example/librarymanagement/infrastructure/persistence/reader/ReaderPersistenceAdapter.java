package org.example.librarymanagement.infrastructure.persistence.reader;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.librarymanagement.domain.entity.Reader;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.outbound.reader.ReaderRepositoryPort;
import org.example.librarymanagement.port.outbound.reader.ReaderSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReaderPersistenceAdapter implements ReaderRepositoryPort {

    private final ReaderJpaRepository readerJpaRepository;
    private final ReaderPersistenceMapper readerPersistenceMapper;

    @Override
public Optional<Reader> findById(Long readerId) {
    return readerJpaRepository.findById(readerId)
            .map(readerPersistenceMapper::toDomain);
}

@Override
public Reader save(Reader reader) {
    ReaderJpaEntity entity =
            readerPersistenceMapper.toJpaEntity(reader);

    ReaderJpaEntity savedEntity =
            readerJpaRepository.save(entity);

    return readerPersistenceMapper.toDomain(savedEntity);
}
    @Override
    public Optional<Reader> findByCardNumber(String cardNumber) {
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
    public PageResult<Reader> search(ReaderSearchCriteria criteria) {
        ReaderSearchCriteria safeCriteria = normalizeCriteria(criteria);
        Pageable pageable = PageRequest.of(
                safeCriteria.page(),
                safeCriteria.size()
        );

        Page<ReaderJpaEntity> jpaPage = readerJpaRepository.findAll(
                ReaderSpecification.from(safeCriteria),
                pageable
        );

        List<Reader> content = jpaPage.getContent().stream()
                .map(readerPersistenceMapper::toDomain)
                .collect(Collectors.toList());

        return PageResult.of(
                content,
                jpaPage.getNumber(),
                jpaPage.getSize(),
                jpaPage.getTotalElements()
        );
    }

    @Override
    public List<Reader> findAll() {
        return readerJpaRepository.findByIsActiveTrue().stream()
                .map(readerPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reader> findByCreatedByUserId(Long createdByUserId) {
        return readerJpaRepository.findByCreatedByUserIdAndIsActiveTrue(createdByUserId).stream()
                .map(readerPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<Reader> findAll(int page, int size) {
        return search(new ReaderSearchCriteria(
                null,
                null,
                null,
                page,
                size
        ));
    }

    @Override
    public PageResult<Reader> findByCreatedByUserId(Long createdByUserId, int page, int size) {
        return search(new ReaderSearchCriteria(
                null,
                null,
                createdByUserId,
                page,
                size
        ));
    }

    private ReaderSearchCriteria normalizeCriteria(
            ReaderSearchCriteria criteria
    ) {
        if (criteria == null) {
            return new ReaderSearchCriteria(
                    null,
                    null,
                    null,
                    ReaderSearchCriteria.DEFAULT_PAGE,
                    ReaderSearchCriteria.DEFAULT_PAGE_SIZE
            );
        }

        int safePage = Math.max(
                criteria.page(),
                0
        );

        int safeSize = criteria.size() <= 0
                ? ReaderSearchCriteria.DEFAULT_PAGE_SIZE
                : Math.min(
                        criteria.size(),
                        ReaderSearchCriteria.MAX_PAGE_SIZE
                );

        return new ReaderSearchCriteria(
                criteria.keyword(),
                criteria.status(),
                criteria.createdByUserId(),
                safePage,
                safeSize
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
