package org.example.librarymanagement.port.inbound.reader;

import java.util.List;

import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.dtos.reader.CreateReaderCommand;
import org.example.librarymanagement.port.dtos.reader.ReaderResult;
import org.example.librarymanagement.port.dtos.reader.UpdateReaderCommand;

public interface ReaderManagementUseCase {

    ReaderResult createReader(CreateReaderCommand command);

    ReaderResult updateReader(UpdateReaderCommand command);
    
    List<ReaderResult> getAllReaders();

    PageResult<ReaderResult> getAllReaders(int page, int size);
}
