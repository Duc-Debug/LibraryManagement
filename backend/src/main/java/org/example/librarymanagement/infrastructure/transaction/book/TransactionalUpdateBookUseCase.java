package org.example.librarymanagement.infrastructure.transaction.book;

import java.util.Objects;

import org.example.librarymanagement.port.dtos.book.BookResult;
import org.example.librarymanagement.port.dtos.book.UpdateBookCommand;
import org.example.librarymanagement.port.inbound.book.UpdateBookUseCase;
import org.example.librarymanagement.port.outbound.file.FileStoragePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Transactional Decorator Proxy for UpdateBookUseCase
 * Bọc Spring @Transactional ngoài Pure Java UpdateBookService.
 * Đăng ký TransactionSynchronization để dọn dẹp ảnh mới vừa upload trên Cloud R2 nếu DB Transaction bị Rollback ở bước Commit time.
 */
public class TransactionalUpdateBookUseCase implements UpdateBookUseCase {

    private static final Logger log = LoggerFactory.getLogger(TransactionalUpdateBookUseCase.class);

    private final UpdateBookUseCase delegate;
    private final FileStoragePort fileStoragePort;

    public TransactionalUpdateBookUseCase(
            UpdateBookUseCase delegate,
            FileStoragePort fileStoragePort
    ) {
        this.delegate = Objects.requireNonNull(delegate, "UpdateBookUseCase delegate must not be null");
        this.fileStoragePort = Objects.requireNonNull(fileStoragePort, "FileStoragePort must not be null");
    }

    @Override
    @Transactional
    public BookResult updateBook(UpdateBookCommand command) {
        BookResult result = delegate.updateBook(command);

        if (command.imageStream() != null && result.coverImageUrl() != null) {
            registerNewImageCleanupOnRollback(result.coverImageUrl());
        }

        return result;
    }

    private void registerNewImageCleanupOnRollback(String newImageUrl) {
        if (newImageUrl == null || newImageUrl.isBlank()) {
            return;
        }

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status != STATUS_COMMITTED) {
                            cleanupImage(newImageUrl);
                        }
                    }
                }
        );
    }

    private void cleanupImage(String imageUrl) {
        try {
            fileStoragePort.deleteFile(imageUrl);
            log.info("Successfully cleaned up new image after transaction rollback: {}", imageUrl);
        } catch (Exception e) {
            log.error("Failed to cleanup image after transaction rollback: {}", imageUrl, e);
        }
    }
}
