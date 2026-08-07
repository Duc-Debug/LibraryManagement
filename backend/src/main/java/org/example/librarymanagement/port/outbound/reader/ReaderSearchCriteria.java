package org.example.librarymanagement.port.outbound.reader;

import org.example.librarymanagement.domain.enums.CardStatus;

public record ReaderSearchCriteria(
        String keyword,
        CardStatus status,
        Long createdByUserId,
        int page,
        int size
) {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
}
