package org.example.librarymanagement.port.outbound.file;

import java.util.List;

import org.example.librarymanagement.domain.entity.FileCleanupTask;

public interface FileCleanupPort {

    void queueFileForDeletion(String fileUrl);

    List<FileCleanupTask> findPendingTasks(int limit);

    void markCompleted(Long taskId);

    void incrementRetry(Long taskId, int maxRetries);
}
