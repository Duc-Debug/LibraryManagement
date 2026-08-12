package org.example.librarymanagement.infrastructure.persistence.borrow;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

@Repository
public interface BorrowDetailsJpaRepository
        extends JpaRepository<BorrowDetailsJpaEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT bd
            FROM BorrowDetailsJpaEntity bd
            WHERE bd.borrowSlipId = :borrowSlipId
            ORDER BY bd.id
            """)
    List<BorrowDetailsJpaEntity> findByBorrowSlipIdForUpdate(
            @Param("borrowSlipId") Long borrowSlipId
    );
}