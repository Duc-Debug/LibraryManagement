package org.example.librarymanagement.infrastructure.scheduler;

import java.util.List;
import java.util.Objects;

import org.example.librarymanagement.domain.entity.FileCleanupTask;
import org.example.librarymanagement.port.outbound.file.FileCleanupPort;
import org.example.librarymanagement.port.outbound.file.FileStoragePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Background Scheduler cho Transactional Outbox Pattern.
 * Định kỳ quét các tác vụ PENDING trong bảng file_cleanup_tasks và gọi Cloud R2 để xóa tệp.
 */
@Component
public class CloudFileCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(CloudFileCleanupScheduler.class);
    private static final int MAX_RETRIES = 5;

    private final FileCleanupPort fileCleanupPort;
    private final FileStoragePort fileStoragePort;

    public CloudFileCleanupScheduler(
            FileCleanupPort fileCleanupPort,
            FileStoragePort fileStoragePort
    ) {
        this.fileCleanupPort = Objects.requireNonNull(fileCleanupPort, "FileCleanupPort must not be null");
        this.fileStoragePort = Objects.requireNonNull(fileStoragePort, "FileStoragePort must not be null");
    }

    /**
     * Chạy định kỳ ngầm mỗi 5 phút (có thể override qua app.scheduler.file-cleanup.cron).
     */
    @Scheduled(cron = "${app.scheduler.file-cleanup.cron:0 */5 * * * *}")
    public void processPendingCleanups() {
        List<FileCleanupTask> pendingTasks = fileCleanupPort.findPendingTasks(20);
        if (pendingTasks.isEmpty()) {
            return;
        }

        log.info("[Cloud Cleanup Scheduler] Phát hiện {} tác vụ dọn dẹp tệp Cloud chờ xử lý...", pendingTasks.size());

        for (FileCleanupTask task : pendingTasks) {
            try {
                fileStoragePort.deleteFile(task.getFileUrl());
                fileCleanupPort.markCompleted(task.getId());
                log.info("[Cloud Cleanup Scheduler] Đã xóa tệp Cloud thành công: {}", task.getFileUrl());
            } catch (Exception e) {
                log.error("[Cloud Cleanup Scheduler] Thất bại khi xóa tệp Cloud {} (Lần thử: {}): {}",
                        task.getFileUrl(), task.getRetryCount() + 1, e.getMessage(), e);
                fileCleanupPort.incrementRetry(task.getId(), MAX_RETRIES);
            }
        }
    }
}
