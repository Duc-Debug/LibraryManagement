package org.example.librarymanagement.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class FileCleanupTask {

    private final Long id;
    private final String fileUrl;
    private FileCleanupStatus status;
    private int retryCount;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FileCleanupTask(
            Long id,
            String fileUrl,
            FileCleanupStatus status,
            int retryCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.fileUrl = Objects.requireNonNull(fileUrl, "fileUrl must not be null");
        this.status = status != null ? status : FileCleanupStatus.PENDING;
        this.retryCount = retryCount;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public static FileCleanupTask createPending(String fileUrl) {
        return new FileCleanupTask(null, fileUrl, FileCleanupStatus.PENDING, 0, LocalDateTime.now(), LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public FileCleanupStatus getStatus() {
        return status;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void markCompleted() {
        this.status = FileCleanupStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementRetry(int maxRetries) {
        this.retryCount++;
        this.updatedAt = LocalDateTime.now();
        if (this.retryCount >= maxRetries) {
            this.status = FileCleanupStatus.FAILED;
        }
    }
}
