package org.example.librarymanagement.infrastructure.persistence.book;

import java.util.List;
import java.util.Optional;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.port.inbound.common.PageResult;
import org.example.librarymanagement.port.outbound.book.LoadBookPort;
import org.example.librarymanagement.port.outbound.book.SaveBookPort;
import org.example.librarymanagement.port.outbound.borrow.CheckActiveBorrowPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class BookPersistenceAdapter implements LoadBookPort, SaveBookPort, CheckActiveBorrowPort {
    private final BookJpaRepository bookJpaRepository;

    public BookPersistenceAdapter(BookJpaRepository bookJpaRepository) {
        this.bookJpaRepository = bookJpaRepository;
    }

    @Override
    public boolean hasActiveBorrowSlips(Long bookId) {
        return bookJpaRepository.countActiveBorrowByBookId(bookId) > 0;
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

    @Override
    public PageResult<Book> findAll(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookJpaEntity> jpaPage;

        if (keyword == null || keyword.trim().isEmpty()) {
            jpaPage = bookJpaRepository.findAll(pageable);
        } else {
            String search = keyword.trim();
            jpaPage = bookJpaRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(search, search, pageable);
        }

        List<Book> domainBooks = jpaPage.getContent().stream()
                .map(BookPersistenceMapper::toDomain)
                .toList();

        return new PageResult<>(
                domainBooks,
                jpaPage.getNumber(),
                jpaPage.getSize(),
                jpaPage.getTotalElements(),
                jpaPage.getTotalPages()
        );
    }
}
