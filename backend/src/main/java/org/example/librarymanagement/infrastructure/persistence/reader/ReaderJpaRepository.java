package org.example.librarymanagement.infrastructure.persistence.reader;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReaderJpaRepository extends JpaRepository<ReaderJpaEntity, Long> {

    Optional<ReaderJpaEntity> findByCardNumber(String cardNumber);

    boolean existsByCardNumber(String cardNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
