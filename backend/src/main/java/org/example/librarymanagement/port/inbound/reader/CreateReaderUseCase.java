package org.example.librarymanagement.port.inbound.reader;

import org.example.librarymanagement.port.dtos.reader.CreateReaderCommand;
import org.example.librarymanagement.port.dtos.reader.CreateReaderResult;

import java.util.List;

public interface CreateReaderUseCase {

    CreateReaderResult createReader(CreateReaderCommand command);

    List<CreateReaderResult> getAllReaders();
}
