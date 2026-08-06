package org.example.librarymanagement.port.inbound.book;

import org.example.librarymanagement.port.inbound.common.PageResult;

public interface GetBooksUseCase {
    PageResult<BookResponseDto> getBooks(int page,int size,String keyword);
    BookResponseDto getBookById(Long bookId);
}
