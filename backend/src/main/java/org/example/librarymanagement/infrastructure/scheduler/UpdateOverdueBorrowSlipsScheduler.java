package org.example.librarymanagement.infrastructure.scheduler;

import org.example.librarymanagement.port.inbound.borrow.UpdateOverdueBorrowSlipsUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateOverdueBorrowSlipsScheduler {

    private final UpdateOverdueBorrowSlipsUseCase updateOverdueBorrowSlipsUseCase;

    @Scheduled(fixedRate = 60000) // Chạy mỗi 1 phút
    public void updateOverdueBorrowSlips() {
        log.debug("Starting overdue borrow slip update");
        int updatedCount = updateOverdueBorrowSlipsUseCase.updateOverdueSlips();
        if (updatedCount > 0) {
            log.info("Marked {} borrow slips as overdue", updatedCount);
        }
    }
}
