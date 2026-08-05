package org.example.librarymanagement.infrastructure.persistence.book;

import java.util.Optional;
import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.port.outbound.borrow.CheckActiveBorrowPort;
import org.example.librarymanagement.port.outbound.book.LoadBookPort;
import org.example.librarymanagement.port.outbound.book.SaveBookPort;
import org.springframework.stereotype.Component;

@Component
public class BookPersistenceAdapter implements LoadBookPort, SaveBookPort, CheckActiveBorrowPort {
    private final BookJpaRepository bookJpaRepository;
    private final BorrowDetailsJpaRepository borrowDetailsJpaRepository;

    public BookPersistenceAdapter(BookJpaRepository bookJpaRepository,
            BorrowDetailsJpaRepository borrowDetailsJpaRepository) {
        this.bookJpaRepository = bookJpaRepository;
        this.borrowDetailsJpaRepository = borrowDetailsJpaRepository;
    }

    @Override
    public boolean hasActiveBorrowSlips(Long bookId) {
       return borrowDetailsJpaRepository.existsActiveBorrowByBookId(bookId);
    }

    @Override
    public Book save(Book book) {
        BookJpaEntity entity = BookPersistenceMapper.toJpaEntity(book);
        BookJpaEntity saveEntity = bookJpaRepository.save(entity);
        return BookPersistenceMapper.toDomain(saveEntity);
    }

    @Override
    public void deleteById(Long bookId) {
       bookJpaRepository.deleteById(bookId);
    }

    @Override
    public Optional<Book> findById(Long bookId) {
       return bookJpaRepository.findById(bookId)
            .map(BookPersistenceMapper::toDomain);
    }

}
