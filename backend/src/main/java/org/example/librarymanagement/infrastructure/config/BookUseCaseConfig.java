package org.example.librarymanagement.infrastructure.config;

import org.example.librarymanagement.application.book.DeleteBookService;
import org.example.librarymanagement.application.book.GetBooksService;
import org.example.librarymanagement.application.book.UpdateBookService;
import org.example.librarymanagement.application.book.ReplenishBookStockService;
import org.example.librarymanagement.infrastructure.transaction.book.TransactionalDeleteBookUseCase;
import org.example.librarymanagement.infrastructure.transaction.book.TransactionalReplenishBookStockUseCase;
import org.example.librarymanagement.infrastructure.transaction.book.TransactionalUpdateBookUseCase;
import org.example.librarymanagement.port.inbound.book.DeleteBookUseCase;
import org.example.librarymanagement.port.inbound.book.GetBooksUseCase;
import org.example.librarymanagement.port.inbound.book.UpdateBookUseCase;
import org.example.librarymanagement.port.inbound.book.ReplenishBookStockUseCase;
import org.example.librarymanagement.port.outbound.book.BookRepositoryPort;
import org.example.librarymanagement.port.outbound.book.LoadBookPort;
import org.example.librarymanagement.port.outbound.book.SaveBookPort;
import org.example.librarymanagement.port.outbound.borrow.CheckActiveBorrowPort;
import org.example.librarymanagement.port.outbound.category.LoadCategoryPort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookUseCaseConfig {

    @Bean
    public DeleteBookUseCase deleteBookUseCase(
            LoadBookPort loadBookPort,
            SaveBookPort saveBookPort,
            CheckActiveBorrowPort checkActiveBorrowPort) {
        DeleteBookUseCase deleteBookService = new DeleteBookService(loadBookPort, saveBookPort, checkActiveBorrowPort);
        return new TransactionalDeleteBookUseCase(deleteBookService);
    }

    @Bean
    public GetBooksUseCase getBooksUseCase(LoadBookPort loadBookPort, LoadCategoryPort loadCategoryPort) {
        return new GetBooksService(loadBookPort, loadCategoryPort);
    }

    @Bean
    public UpdateBookUseCase updateBookUseCase(
            BookRepositoryPort bookRepository,
            GetAuthenticatedUserPort getAuthenticatedUserPort) {
        UpdateBookService service = new UpdateBookService(bookRepository, getAuthenticatedUserPort);
        return new TransactionalUpdateBookUseCase(service);
    }

    @Bean
    public ReplenishBookStockUseCase replenishBookStockUseCase(
            BookRepositoryPort bookRepository,
            GetAuthenticatedUserPort getAuthenticatedUserPort) {
        ReplenishBookStockUseCase service = new ReplenishBookStockService(bookRepository, getAuthenticatedUserPort);
        return new TransactionalReplenishBookStockUseCase(service);
    }
}
