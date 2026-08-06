package org.example.librarymanagement.infrastructure.persistence.reader;

import java.util.List;
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

    List<ReaderJpaEntity> findByIsActiveTrue();

    Page<ReaderJpaEntity> findByIsActiveTrue(Pageable pageable);

    List<ReaderJpaEntity> findByCreatedByUserIdAndIsActiveTrue(Long createdByUserId);

    Page<ReaderJpaEntity> findByCreatedByUserIdAndIsActiveTrue(Long createdByUserId, Pageable pageable);

    boolean existsByEmailIgnoreCaseAndIdNot(
            String email,
            Long excludedReaderId
    );

    boolean existsByPhoneNumberAndIdNot(
            String phoneNumber,
            Long excludedReaderId
    );
}
