package org.example.librarymanagement.infrastructure.web.reader;

import org.example.librarymanagement.port.dtos.reader.CreateReaderCommand;
import org.example.librarymanagement.port.dtos.reader.CreateReaderResult;
import org.example.librarymanagement.port.inbound.reader.CreateReaderUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/readers")
@RequiredArgsConstructor
public class ReaderManagementController {

    private final CreateReaderUseCase createReaderUseCase;

    @PostMapping
    public ResponseEntity<ReaderResponse> createReader(@Valid @RequestBody CreateReaderRequest request) {
        CreateReaderCommand command = new CreateReaderCommand(
                request.name(),
                request.email(),
                request.phoneNumber(),
                request.address()
        );

        CreateReaderResult result = createReaderUseCase.createReader(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ReaderResponse.fromResult(result));
    }

    @org.springframework.web.bind.annotation.GetMapping
    public ResponseEntity<java.util.List<ReaderResponse>> getAllReaders() {
        java.util.List<ReaderResponse> list = createReaderUseCase.getAllReaders().stream()
                .map(ReaderResponse::fromResult)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(list);
    }
}
