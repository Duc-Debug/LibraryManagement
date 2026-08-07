package org.example.librarymanagement.infrastructure.persistence.reader;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReaderJpaRepository extends JpaRepository<ReaderJpaEntity, Long> {

    Optional<ReaderJpaEntity> findByCardNumber(String cardNumber);

    boolean existsByCardNumber(String cardNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    java.util.List<ReaderJpaEntity> findByCreatedByUserId(Long createdByUserId);

    Page<ReaderJpaEntity> findByCreatedByUserId(Long createdByUserId, Pageable pageable);
}
