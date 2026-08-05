package org.example.librarymanagement.infrastructure.config;

import org.example.librarymanagement.application.book.DeleteBookService;
import org.example.librarymanagement.port.inbound.book.DeleteBookUseCase;
import org.example.librarymanagement.port.outbound.borrow.CheckActiveBorrowPort;
import org.example.librarymanagement.port.outbound.book.LoadBookPort;
import org.example.librarymanagement.port.outbound.book.SaveBookPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.transaction.Transactional;

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
}
