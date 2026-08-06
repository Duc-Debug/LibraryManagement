package org.example.librarymanagement.infrastructure.web.reader;

import org.example.librarymanagement.port.dtos.reader.CreateReaderCommand;
import org.example.librarymanagement.port.dtos.reader.ReaderResult;
import org.example.librarymanagement.port.inbound.reader.ReaderManagementUseCase;
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

    @org.springframework.web.bind.annotation.GetMapping
    public ResponseEntity<Object> getAllReaders(
            @org.springframework.web.bind.annotation.RequestParam(required = false) Integer page,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Integer size
    ) {
        if (page != null && size != null) {
            org.example.librarymanagement.port.dtos.common.PageResult<ReaderResult> pageResult =
                    readerManagementUseCase.getAllReaders(page, size);

            java.util.List<ReaderResponse> content = pageResult.content().stream()
                    .map(ReaderResponse::fromResult)
                    .collect(java.util.stream.Collectors.toList());

            org.example.librarymanagement.port.dtos.common.PageResult<ReaderResponse> responsePage =
                    org.example.librarymanagement.port.dtos.common.PageResult.of(
                            content,
                            pageResult.page(),
                            pageResult.size(),
                            pageResult.totalElements()
                    );
            return ResponseEntity.ok(responsePage);
        }

        java.util.List<ReaderResponse> list = readerManagementUseCase.getAllReaders().stream()
                .map(ReaderResponse::fromResult)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(list);
    }
}
