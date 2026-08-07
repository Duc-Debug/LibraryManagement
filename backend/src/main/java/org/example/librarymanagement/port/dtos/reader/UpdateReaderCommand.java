package org.example.librarymanagement.port.dtos.reader;

public record UpdateReaderCommand(
        Long readerId,
        String name,
        String email,
        String phoneNumber,
        String address
) {
}