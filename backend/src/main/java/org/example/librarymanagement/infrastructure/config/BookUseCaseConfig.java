package org.example.librarymanagement.infrastructure.config;

import org.example.librarymanagement.application.book.DeleteBookService;
import org.example.librarymanagement.application.book.GetBooksService;
import org.example.librarymanagement.port.inbound.book.DeleteBookUseCase;
import org.example.librarymanagement.port.inbound.book.GetBooksUseCase;
import org.example.librarymanagement.port.outbound.borrow.CheckActiveBorrowPort;
import org.example.librarymanagement.port.outbound.book.LoadBookPort;
import org.example.librarymanagement.port.outbound.book.SaveBookPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class BookUseCaseConfig {
    
    @Bean
    @Transactional
    public DeleteBookUseCase deleteBookUseCase(
        LoadBookPort loadBookPort,
        SaveBookPort saveBookPort,
        CheckActiveBorrowPort checkActiveBorrowPort
    ){
        return new DeleteBookService(loadBookPort, saveBookPort, checkActiveBorrowPort);
    }

    @Bean
    public GetBooksUseCase getBooksUseCase(LoadBookPort loadBookPort){
        return new GetBooksService(loadBookPort);
    }
}
