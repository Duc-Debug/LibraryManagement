package org.example.librarymanagement.infrastructure.transaction.book;

import java.util.Objects;

import org.example.librarymanagement.port.dtos.book.BookResult;
import org.example.librarymanagement.port.inbound.book.CreateBookCommand;
import org.example.librarymanagement.port.inbound.book.CreateBookUseCase;
import org.example.librarymanagement.port.outbound.file.FileStoragePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class TransactionalCreateBookUseCase implements CreateBookUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(TransactionalCreateBookUseCase.class);

    private final CreateBookUseCase delegate;
    private final FileStoragePort fileStoragePort;

    public TransactionalCreateBookUseCase(
            CreateBookUseCase delegate,
            FileStoragePort fileStoragePort
    ) {
        this.delegate = Objects.requireNonNull(
                delegate,
                "CreateBookUseCase delegate must not be null"
        );

        this.fileStoragePort = Objects.requireNonNull(
                fileStoragePort,
                "FileStoragePort must not be null"
        );
    }

    @Override
    @Transactional
    public BookResult createBook(CreateBookCommand command) {

        BookResult result = delegate.createBook(command);

        registerImageCleanupOnRollback(result.coverImageUrl());

        return result;
    }

    private void registerImageCleanupOnRollback(String imageUrl) {

        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }
          if (!TransactionSynchronizationManager.isSynchronizationActive()) {
        throw new IllegalStateException(
                "Transaction synchronization is not active"
        );
    }

    TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status != STATUS_COMMITTED) {
                        cleanupImage(imageUrl);
                    }
                }
            }
    );
    }

    private void cleanupImage(String imageUrl) {
        try {
            fileStoragePort.deleteFile(imageUrl);
        } catch (Exception e) {
            log.error(
                    "Failed to cleanup image after transaction rollback: {}",
                    imageUrl,
                    e
            );
        }
    }
}