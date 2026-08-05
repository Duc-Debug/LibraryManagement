package org.example.librarymanagement.port.inbound.reader;

import java.util.List;

import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.dtos.reader.CreateReaderCommand;
import org.example.librarymanagement.port.dtos.reader.CreateReaderResult;

public interface CreateReaderUseCase {

    CreateReaderResult createReader(CreateReaderCommand command);

    List<CreateReaderResult> getAllReaders();

    PageResult<CreateReaderResult> getAllReaders(int page, int size);
}
