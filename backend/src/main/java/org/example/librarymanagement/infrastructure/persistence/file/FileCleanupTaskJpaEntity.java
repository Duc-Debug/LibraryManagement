package org.example.librarymanagement.infrastructure.persistence.file;

import java.time.LocalDateTime;

import org.example.librarymanagement.domain.entity.FileCleanupStatus;
import org.example.librarymanagement.domain.entity.FileCleanupTask;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "file_cleanup_tasks")
@Getter
@Setter
@NoArgsConstructor
public class FileCleanupTaskJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_url", nullable = false, length = 1000)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FileCleanupStatus status = FileCleanupStatus.PENDING;

    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public FileCleanupTask toDomain() {
        return new FileCleanupTask(
                this.id,
                this.fileUrl,
                this.status,
                this.retryCount,
                this.createdAt,
                this.updatedAt
        );
    }

    public static FileCleanupTaskJpaEntity fromDomain(FileCleanupTask domain) {
        FileCleanupTaskJpaEntity entity = new FileCleanupTaskJpaEntity();
        entity.setId(domain.getId());
        entity.setFileUrl(domain.getFileUrl());
        entity.setStatus(domain.getStatus());
        entity.setRetryCount(domain.getRetryCount());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
