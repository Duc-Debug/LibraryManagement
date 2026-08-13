package org.example.librarymanagement.infrastructure.scheduler;

import java.time.LocalDateTime;
import org.example.librarymanagement.infrastructure.persistence.borrow.BorrowSlipJpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateOverdueBorrowSlipsScheduler {

    private final BorrowSlipJpaRepository borrowSlipJpaRepository;

    @Scheduled(fixedRate = 60000) // Chạy mỗi 1 phút
    @Transactional
    public void updateOverdueBorrowSlips() {
        log.info("Running scheduled task: updateOverdueBorrowSlips");
        try {
            int updatedCount = borrowSlipJpaRepository.updateOverdueStatus(LocalDateTime.now());
            if (updatedCount > 0) {
                log.info("Successfully updated {} borrow slips to OVERDUE", updatedCount);
            }
        } catch (Exception e) {
            log.error("Error occurred while updating overdue borrow slips", e);
        }
    }
}
