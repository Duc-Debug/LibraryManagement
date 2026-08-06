package org.example.librarymanagement.infrastructure.persistence.book;

import java.util.Optional;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.port.outbound.manage.BookRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class BookPersistenceAdapter implements BookRepository {

    private final BookJpaRepository bookJpaRepository;
    private final BookPersistenceMapper bookPersistenceMapper;

    public BookPersistenceAdapter(
            BookJpaRepository bookJpaRepository,
            BookPersistenceMapper bookPersistenceMapper
    ) {
        this.bookJpaRepository = bookJpaRepository;
        this.bookPersistenceMapper = bookPersistenceMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findById(Long id) {
        return bookJpaRepository.findById(id)
                .map(bookPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return bookJpaRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIsbnAndIdNot(String isbn, Long id) {
        return bookJpaRepository.existsByIsbnAndIdNot(isbn, id);
    }

    @Override
    public Book save(Book book) {
        BookJpaEntity entity = bookJpaRepository.findById(book.getBookId())
                .orElseGet(() -> bookPersistenceMapper.toJpaEntity(book));

        bookPersistenceMapper.updateJpaEntity(book, entity);

        BookJpaEntity saved = bookJpaRepository.save(entity);
        return bookPersistenceMapper.toDomain(saved);
    }
}
