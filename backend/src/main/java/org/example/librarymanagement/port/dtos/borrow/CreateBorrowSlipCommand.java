package org.example.librarymanagement.port.dtos.borrow;

import java.util.List;

public record CreateBorrowSlipCommand(
    Long readerId,
    List<Long> bookIds,
    Long createdByUserId,
    Integer borrowDays,
    String note
) {
    public CreateBorrowSlipCommand{
        if(readerId ==null||readerId<=0){
            throw new IllegalArgumentException("Reader Id must be greater than 0");
        }
        if(bookIds ==null ||bookIds.isEmpty()){
            throw new IllegalArgumentException("Book IDs list must not be empty");
        }
        if(createdByUserId==null ||createdByUserId <=0){
            throw new IllegalArgumentException("Created by user Id must be greater than 0");
        }
    }
}
