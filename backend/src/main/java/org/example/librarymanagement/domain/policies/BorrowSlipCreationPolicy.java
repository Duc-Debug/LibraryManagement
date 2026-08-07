package org.example.librarymanagement.domain.policies;

import org.example.librarymanagement.domain.entity.Reader;
import org.example.librarymanagement.domain.enums.CardStatus;
import org.example.librarymanagement.domain.exceptions.DomainException;

public class BorrowSlipCreationPolicy {
    public static void validateCanBorrow(Reader reader) {
        if (!reader.isActive()) {
            throw new DomainException("Độc giả đã bị xóa hoạt động, không thể lập phiếu mượn.");
        }
        if (reader.getCardStatus() == CardStatus.LOCKED) {
            throw new DomainException("Thẻ độc giả đang bị khóa, không thể lập phiếu mượn.");
        }
        if (reader.getCardStatus() == CardStatus.EXPIRED) {
            throw new DomainException("Thẻ độc giả đã hết hạn, không thể lập phiếu mượn.");
        }
    }
}