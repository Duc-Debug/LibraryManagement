package org.example.librarymanagement.infrastructure.persistence.book;

import java.util.List;
import java.util.Optional;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.port.inbound.common.PageResult;
import org.example.librarymanagement.port.outbound.book.LoadBookPort;
import org.example.librarymanagement.port.outbound.book.SaveBookPort;
import org.example.librarymanagement.port.outbound.borrow.CheckActiveBorrowPort;
import org.example.librarymanagement.port.outbound.manage.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BookPersistenceAdapter implements LoadBookPort, SaveBookPort, CheckActiveBorrowPort, BookRepository {

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
    public boolean hasActiveBorrowSlips(Long bookId) {
        return bookJpaRepository.existsActiveBorrowByBookId(bookId) > 0;
    }

    @Override
    public Book save(Book book) {
        BookJpaEntity entity = bookJpaRepository.findById(book.getBookId())
                .orElseGet(() -> bookPersistenceMapper.toJpaEntity(book));

        bookPersistenceMapper.updateJpaEntity(book, entity);

        BookJpaEntity saved = bookJpaRepository.save(entity);
        return bookPersistenceMapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long bookId) {
        bookJpaRepository.deleteById(bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findById(Long bookId) {
        return bookJpaRepository.findById(bookId)
                .map(bookPersistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return bookJpaRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIsbnAndIdNot(String isbn, Long id) {
        return bookJpaRepository.existsByIsbnAndIdNot(isbn, id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Book> findAll(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<BookJpaEntity> jpaPage;

        if (keyword == null || keyword.trim().isEmpty()) {
            jpaPage = bookJpaRepository.findAll(pageable);
        } else {
            String search = keyword.trim();
            jpaPage = bookJpaRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCase(
                    search, search, search, pageable
            );
        }

        List<Book> domainBooks = jpaPage.getContent().stream()
                .map(bookPersistenceMapper::toDomain)
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