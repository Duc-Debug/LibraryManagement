package org.example.librarymanagement.infrastructure.web.reader;

import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.dtos.reader.CreateReaderCommand;
import org.example.librarymanagement.port.dtos.reader.ReaderResult;
import org.example.librarymanagement.port.dtos.reader.UpdateReaderCommand;
import org.example.librarymanagement.port.inbound.reader.ReaderManagementUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@DeleteMapping("/{readerId}")
public ResponseEntity<Void> deleteReader(
        @PathVariable Long readerId
) {
    readerManagementUseCase.deleteReader(readerId);

    return ResponseEntity.noContent().build();
}
   
    @GetMapping
    public ResponseEntity<PageResult<ReaderResponse>> getAllReaders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageResult<ReaderResult> resultPage =
                readerManagementUseCase.getAllReaders(
                        page,
                        size
                );

        var content =
                resultPage.content().stream()
                        .map(ReaderResponse::fromResult)
                        .toList();

        return ResponseEntity.ok(
                PageResult.of(
                        content,
                        resultPage.page(),
                        resultPage.size(),
                        resultPage.totalElements()
                )
        );
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
