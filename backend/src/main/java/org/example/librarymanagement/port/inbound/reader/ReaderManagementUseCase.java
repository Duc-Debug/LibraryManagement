package org.example.librarymanagement.port.inbound.reader;

import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.dtos.reader.ChangeCardStatusCommand;
import org.example.librarymanagement.port.dtos.reader.CreateReaderCommand;
import org.example.librarymanagement.port.dtos.reader.ReaderResult;
import org.example.librarymanagement.port.dtos.reader.UpdateReaderCommand;

public interface ReaderManagementUseCase {

    ReaderResult createReader(CreateReaderCommand command);

    ReaderResult updateReader(UpdateReaderCommand command);

    void deleteReader(Long readerId);

    PageResult<ReaderResult> getAllReaders(int page, int size);

    ReaderResult changeCardStatus(ChangeCardStatusCommand command);
}
