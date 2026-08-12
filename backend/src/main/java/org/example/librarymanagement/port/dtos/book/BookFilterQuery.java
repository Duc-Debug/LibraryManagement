package org.example.librarymanagement.port.dtos.book;

public record BookFilterQuery(
        int page,
        int size,
        String keyword,
        Long categoryId,
        BookAvailabilityStatus availability
        ) {

}
