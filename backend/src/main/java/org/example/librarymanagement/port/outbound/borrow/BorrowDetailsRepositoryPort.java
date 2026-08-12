package org.example.librarymanagement.port.outbound.borrow;

import java.util.List;

import org.example.librarymanagement.domain.entity.BorrowDetails;

public interface BorrowDetailsRepositoryPort {

    List<BorrowDetails> findByBorrowSlipIdForUpdate(Long borrowSlipId);

//lưu returnedAt / returnedBy / fine của các sách sau khi trả
    List<BorrowDetails> saveAll(List<BorrowDetails> borrowDetails);
}