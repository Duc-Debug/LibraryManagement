package org.example.librarymanagement.infrastructure.web.reader;

import java.util.List;

import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.dtos.reader.CreateReaderCommand;
import org.example.librarymanagement.port.dtos.reader.ReaderResult;
import org.example.librarymanagement.port.dtos.reader.UpdateReaderCommand;
import org.example.librarymanagement.port.inbound.reader.ReaderManagementUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/readers")
@RequiredArgsConstructor
public class ReaderManagementController {

    private final ReaderManagementUseCase readerManagementUseCase;

    @PostMapping
    public ResponseEntity<ReaderResponse> createReader(@Valid @RequestBody CreateReaderRequest request) {
        CreateReaderCommand command = new CreateReaderCommand(
                request.name(),
                request.email(),
                request.phoneNumber(),
                request.address()
        );

        ReaderResult result = readerManagementUseCase.createReader(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ReaderResponse.fromResult(result));
    }

   
    @GetMapping
public ResponseEntity<?> getAllReaders(
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size
) {
    if (page != null || size != null) {
        if (page == null || size == null) {
            throw new IllegalArgumentException(
                    "Page and size must be provided together."
            );
        }

        PageResult<ReaderResult> resultPage =
                readerManagementUseCase.getAllReaders(
                        page,
                        size
                );

        List<ReaderResponse> content =
                resultPage.content().stream()
                        .map(ReaderResponse::fromResult)
                        .toList();

        PageResult<ReaderResponse> responsePage =
                PageResult.of(
                        content,
                        resultPage.page(),
                        resultPage.size(),
                        resultPage.totalElements()
                );

        return ResponseEntity.ok(responsePage);
    }

    List<ReaderResponse> response =
            readerManagementUseCase.getAllReaders().stream()
                    .map(ReaderResponse::fromResult)
                    .toList();

    return ResponseEntity.ok(response);
}
    
    @PutMapping("/{readerId}")
public ResponseEntity<ReaderResponse> updateReader(
        @PathVariable Long readerId,
        @Valid @RequestBody UpdateReaderRequest request
) {
    UpdateReaderCommand command = new UpdateReaderCommand(
            readerId,
            request.name(),
            request.email(),
            request.phoneNumber(),
            request.address()
    );

    ReaderResult result =
            readerManagementUseCase.updateReader(command);

    return ResponseEntity.ok(
            ReaderResponse.fromResult(result)
    );
}
}
