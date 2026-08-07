package org.example.librarymanagement.infrastructure.persistence.book;

import java.util.List;
import java.util.Optional;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.port.inbound.common.PageResult;
import org.example.librarymanagement.port.outbound.book.LoadBookPort;
import org.example.librarymanagement.port.outbound.borrow.CheckActiveBorrowPort;
import org.example.librarymanagement.port.outbound.book.FindBookPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class BookPersistenceAdapter implements 
        LoadBookPort, 
        org.example.librarymanagement.port.outbound.book.SaveBookPort,
        CheckActiveBorrowPort, 
        FindBookPort {

    private final BookJpaRepository repository;
    private final BookPersistenceMapper mapper;

    public BookPersistenceAdapter(BookJpaRepository repository, BookPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    // ==================== CHECK ACTIVE BORROW PORT ====================

    @Override
    public boolean hasActiveBorrowSlips(Long bookId) {
        return repository.existsActiveBorrowByBookId(bookId) > 0;
    }

    // ==================== SAVE BOOK PORT ====================

    @Override
    public Book save(Book book) {
        BookJpaEntity entity = mapper.toJpaEntity(book);
        BookJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long bookId) {
        repository.deleteById(bookId);
    }

    // ==================== LOAD BOOK PORT ====================

    @Override
    public Optional<Book> findById(Long bookId) {
        return repository.findById(bookId)
                .map(mapper::toDomain);
    }

    @Override
    public PageResult<Book> findAll(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,"id"));
        Page<BookJpaEntity> jpaPage;

        if (keyword == null || keyword.trim().isEmpty()) {
            jpaPage = repository.findAll(pageable);
        } else {
            String search = keyword.trim();
            jpaPage = repository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCase(
                    search, search, search, pageable
            );
        }

        List<Book> domainBooks = jpaPage.getContent().stream()
                .map(mapper::toDomain)
                .toList();

        return new PageResult<>(
                domainBooks,
                jpaPage.getNumber(),
                jpaPage.getSize(),
                jpaPage.getTotalElements(),
                jpaPage.getTotalPages()
        );
    }

    // ==================== FIND BOOK PORT ====================

    @Override
    public boolean existsByIsbn(String isbn) {
        return repository.existsByIsbnIgnoreCase(isbn.trim());
    }

    @Override
    public List<Book> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}