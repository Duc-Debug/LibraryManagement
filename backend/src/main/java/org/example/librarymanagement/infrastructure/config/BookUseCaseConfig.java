package org.example.librarymanagement.infrastructure.config;

import org.example.librarymanagement.application.book.DeleteBookService;
import org.example.librarymanagement.application.book.GetBooksService;
import org.example.librarymanagement.application.book.UpdateBookService;
import org.example.librarymanagement.application.book.ReplenishBookStockService;
import org.example.librarymanagement.infrastructure.transaction.book.TransactionalDeleteBookUseCase;
import org.example.librarymanagement.port.inbound.book.DeleteBookUseCase;
import org.example.librarymanagement.port.inbound.book.GetBooksUseCase;
import org.example.librarymanagement.port.inbound.book.UpdateBookUseCase;
import org.example.librarymanagement.port.inbound.book.ReplenishBookStockUseCase;
import org.example.librarymanagement.port.outbound.book.BookRepository;
import org.example.librarymanagement.port.outbound.book.LoadBookPort;
import org.example.librarymanagement.port.outbound.book.SaveBookPort;
import org.example.librarymanagement.port.outbound.borrow.CheckActiveBorrowPort;
import org.example.librarymanagement.port.outbound.category.LoadCategoryPort;
import org.example.librarymanagement.port.outbound.manage.GetAuthenticatedUserPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookUseCaseConfig {

    @Bean
    public DeleteBookUseCase deleteBookUseCase(
            LoadBookPort loadBookPort,
            SaveBookPort saveBookPort,
            CheckActiveBorrowPort checkActiveBorrowPort
    ) {
        DeleteBookUseCase deleteBookService = new DeleteBookService(loadBookPort, saveBookPort, checkActiveBorrowPort);
        return new TransactionalDeleteBookUseCase(deleteBookService);
    }

    @Bean
    public GetBooksUseCase getBooksUseCase(LoadBookPort loadBookPort, LoadCategoryPort loadCategoryPort) {
        return new GetBooksService(loadBookPort, loadCategoryPort);
    }

    @Bean
    public UpdateBookUseCase updateBookUseCase(
            BookRepository bookRepository,
            GetAuthenticatedUserPort getAuthenticatedUserPort
    ) {
        return new UpdateBookService(bookRepository, getAuthenticatedUserPort);
    }

    @Bean
    public ReplenishBookStockUseCase replenishBookStockUseCase(
            BookRepository bookRepository,
            GetAuthenticatedUserPort getAuthenticatedUserPort
    ) {
        return new ReplenishBookStockService(bookRepository, getAuthenticatedUserPort);
    }
}
