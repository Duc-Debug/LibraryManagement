package org.example.librarymanagement.port.outbound.borrow;

import java.time.LocalDateTime;

public interface UpdateOverdueBorrowSlipsPort {
    int updateOverdueStatus(LocalDateTime asOfDate);
}
