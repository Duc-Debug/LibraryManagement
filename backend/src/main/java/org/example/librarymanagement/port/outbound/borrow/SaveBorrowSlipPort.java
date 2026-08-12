package org.example.librarymanagement.port.outbound.borrow;

import org.example.librarymanagement.domain.entity.BorrowSlip;

public interface SaveBorrowSlipPort {

    BorrowSlip save(BorrowSlip borrowSlip);
}