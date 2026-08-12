package org.example.librarymanagement.port.dtos.borrow;

import java.util.List;

public record ReaderBorrowEligibilityDto(
        Long readerId,
        String cardNumber,
        String readerName,
        int currentBorrowingCount,
        int maxBorrowLimit,
        int remainingBorrowLimit,
        boolean hasOverdueBooks,
        int overdueBooksCount,
        List<OverdueSlipSummaryDto> overdueSlips,
        boolean eligible,
        String rejectionReason
) {
    public static ReaderBorrowEligibilityDto eligible(
            Long readerId,
            String cardNumber,
            String readerName,
            int currentBorrowingCount,
            int maxBorrowLimit,
            int remainingBorrowLimit
    ) {
        return new ReaderBorrowEligibilityDto(
                readerId,
                cardNumber,
                readerName,
                currentBorrowingCount,
                maxBorrowLimit,
                remainingBorrowLimit,
                false,
                0,
                List.of(),
                true,
                null
        );
    }

    public static ReaderBorrowEligibilityDto rejected(
            Long readerId,
            String cardNumber,
            String readerName,
            int currentBorrowingCount,
            int maxBorrowLimit,
            int remainingBorrowLimit,
            boolean hasOverdueBooks,
            int overdueBooksCount,
            List<OverdueSlipSummaryDto> overdueSlips,
            String rejectionReason
    ) {
        return new ReaderBorrowEligibilityDto(
                readerId,
                cardNumber,
                readerName,
                currentBorrowingCount,
                maxBorrowLimit,
                remainingBorrowLimit,
                hasOverdueBooks,
                overdueBooksCount,
                overdueSlips != null ? overdueSlips : List.of(),
                false,
                rejectionReason
        );
    }
}
