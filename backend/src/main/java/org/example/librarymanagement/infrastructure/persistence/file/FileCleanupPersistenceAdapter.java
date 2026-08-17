package org.example.librarymanagement.infrastructure.persistence.file;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.example.librarymanagement.domain.entity.FileCleanupStatus;
import org.example.librarymanagement.domain.entity.FileCleanupTask;
import org.example.librarymanagement.port.outbound.file.FileCleanupPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FileCleanupPersistenceAdapter implements FileCleanupPort {

    private final FileCleanupTaskJpaRepository repository;

    public FileCleanupPersistenceAdapter(FileCleanupTaskJpaRepository repository) {
        this.repository = Objects.requireNonNull(repository, "FileCleanupTaskJpaRepository must not be null");
    }

    @Override
    @Transactional
    public void queueFileForDeletion(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }
        FileCleanupTask task = FileCleanupTask.createPending(fileUrl);
        repository.save(FileCleanupTaskJpaEntity.fromDomain(task));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileCleanupTask> findPendingTasks(int limit) {
        int fetchSize = limit > 0 ? limit : 20;
        return repository.findByStatusOrderByCreatedAtAsc(FileCleanupStatus.PENDING, PageRequest.of(0, fetchSize))
                .stream()
                .map(FileCleanupTaskJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markCompleted(Long taskId) {
        repository.findById(taskId).ifPresent(entity -> {
            FileCleanupTask task = entity.toDomain();
            task.markCompleted();
            entity.setStatus(task.getStatus());
            entity.setUpdatedAt(task.getUpdatedAt());
            repository.save(entity);
        });
    }

    @Override
    @Transactional
    public void incrementRetry(Long taskId, int maxRetries) {
        repository.findById(taskId).ifPresent(entity -> {
            FileCleanupTask task = entity.toDomain();
            task.incrementRetry(maxRetries);
            entity.setStatus(task.getStatus());
            entity.setRetryCount(task.getRetryCount());
            entity.setUpdatedAt(task.getUpdatedAt());
            repository.save(entity);
        });
    }
}
