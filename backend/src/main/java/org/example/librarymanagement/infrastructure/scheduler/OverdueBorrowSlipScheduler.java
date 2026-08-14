package org.example.librarymanagement.infrastructure.scheduler;

import java.util.Objects;

import org.example.librarymanagement.port.inbound.borrow.UpdateOverdueBorrowSlipsUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler tự động quét và cập nhật phiếu mượn quá hạn.
 *
 * <p>Chạy lúc 01:00 AM mỗi ngày (cron: {@code 0 0 1 * * *}).
 * Thời điểm này đảm bảo chạy sau nửa đêm, sau khi ngày mới đã thực sự bắt đầu,
 * tránh race condition nếu scheduler chạy đúng 00:00:00.</p>
 */
@Component
public class OverdueBorrowSlipScheduler {

    private static final Logger log = LoggerFactory.getLogger(OverdueBorrowSlipScheduler.class);

    private final UpdateOverdueBorrowSlipsUseCase updateOverdueBorrowSlipsUseCase;

    public OverdueBorrowSlipScheduler(UpdateOverdueBorrowSlipsUseCase updateOverdueBorrowSlipsUseCase) {
        this.updateOverdueBorrowSlipsUseCase = Objects.requireNonNull(
                updateOverdueBorrowSlipsUseCase,
                "UpdateOverdueBorrowSlipsUseCase must not be null");
    }

    /**
     * Cron: mỗi ngày lúc 01:00 AM.
     * Có thể override qua {@code app.scheduler.overdue.cron} nếu cần.
     */
    @Scheduled(cron = "${app.scheduler.overdue.cron:0 0 1 * * *}")
    public void updateOverdueSlips() {
        log.info("[Scheduler] Bắt đầu quét phiếu mượn quá hạn...");
        try {
            int updated = updateOverdueBorrowSlipsUseCase.execute();
            log.info("[Scheduler] Đã cập nhật {} phiếu mượn sang trạng thái OVERDUE.", updated);
        } catch (Exception e) {
            log.error("[Scheduler] Lỗi khi cập nhật phiếu mượn quá hạn: {}", e.getMessage(), e);
        }
    }
}
