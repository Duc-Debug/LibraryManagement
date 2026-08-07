package org.example.librarymanagement.infrastructure.transaction.reader;

import java.util.Objects;

import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.dtos.reader.ChangeCardStatusCommand;
import org.example.librarymanagement.port.dtos.reader.CreateReaderCommand;
import org.example.librarymanagement.port.dtos.reader.ReaderResult;
import org.example.librarymanagement.port.dtos.reader.UpdateReaderCommand;
import org.example.librarymanagement.port.inbound.reader.ReaderManagementUseCase;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transactional Decorator Proxy for ReaderManagementUseCase
 * Bọc Spring @Transactional ngoài Pure Java ReaderManagementService
 */
public class TransactionalReaderManagementUseCase implements ReaderManagementUseCase {

    private final ReaderManagementUseCase delegate;

    public TransactionalReaderManagementUseCase(ReaderManagementUseCase delegate) {
        this.delegate = Objects.requireNonNull(delegate, "ReaderManagementUseCase delegate must not be null");
    }

    @Override
    @Transactional
    public ReaderResult createReader(CreateReaderCommand command) {
        return delegate.createReader(command);
    }

    @Override
    @Transactional
    public ReaderResult updateReader(UpdateReaderCommand command) {
        return delegate.updateReader(command);
    }

    @Override
    @Transactional
    public void deleteReader(Long readerId) {
        delegate.deleteReader(readerId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<ReaderResult> getAllReaders(int page, int size) {
        return delegate.getAllReaders(page, size);
    }

    @Override
    @Transactional
    public ReaderResult changeCardStatus(ChangeCardStatusCommand command) {
        return delegate.changeCardStatus(command);
    }
}
