package org.example.librarymanagement.port.dtos.reader;

public record CreateReaderCommand(
        String name,
        String email,
        String phoneNumber,
        String address
        ) {

}
