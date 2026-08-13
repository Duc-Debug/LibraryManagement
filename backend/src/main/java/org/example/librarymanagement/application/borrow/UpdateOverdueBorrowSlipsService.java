package org.example.librarymanagement.application.borrow;

import java.time.LocalDateTime;
import java.util.Objects;

import org.example.librarymanagement.port.inbound.borrow.UpdateOverdueBorrowSlipsUseCase;
import org.example.librarymanagement.port.outbound.borrow.UpdateOverdueBorrowSlipsPort;

public class UpdateOverdueBorrowSlipsService implements UpdateOverdueBorrowSlipsUseCase {

    private final UpdateOverdueBorrowSlipsPort updateOverdueBorrowSlipsPort;

    public UpdateOverdueBorrowSlipsService(UpdateOverdueBorrowSlipsPort updateOverdueBorrowSlipsPort) {
        this.updateOverdueBorrowSlipsPort = Objects.requireNonNull(
                updateOverdueBorrowSlipsPort,
                "UpdateOverdueBorrowSlipsPort must not be null"
        );
    }

    @Override
    public int updateOverdueSlips() {
        return updateOverdueBorrowSlipsPort.updateOverdueStatus(LocalDateTime.now());
    }
}
