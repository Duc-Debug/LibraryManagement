package org.example.librarymanagement.infrastructure.persistence.book;

import java.util.List;
import java.util.Optional;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.outbound.book.LoadBookPort;
import org.example.librarymanagement.port.outbound.book.SaveBookPort;
import org.example.librarymanagement.port.outbound.borrow.CheckActiveBorrowPort;
import org.example.librarymanagement.port.outbound.book.BookRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;


@Repository
public class BookPersistenceAdapter implements LoadBookPort, SaveBookPort, CheckActiveBorrowPort, BookRepositoryPort {

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
        BookJpaEntity entity;

        // 1. Trường hợp TẠO MỚI (bookId == null): Tạo mới hoàn toàn JPA Entity
        if (book.getId() == null) {
            entity = create(book);
        } 
        // 2. Trường hợp CẬP NHẬT (bookId != null): Tìm Entity cũ và cập nhật thông tin
        else {
            entity = update(book);
        }

        BookJpaEntity saved = bookJpaRepository.save(entity);
        return bookPersistenceMapper.toDomain(saved);
    }

    private BookJpaEntity create(Book book) {
        return bookPersistenceMapper.toJpaEntity(book);
    }

    private BookJpaEntity update(Book book) {
        BookJpaEntity entity = bookJpaRepository.findById(book.getId())
                .orElseGet(() -> bookPersistenceMapper.toJpaEntity(book));
        
        // Chỉ cập nhật các trường thông tin đối với trường hợp Update
        bookPersistenceMapper.updateJpaEntity(book, entity);
        return entity;
    }

    @Override
    public void deleteById(Long bookId) {
        bookJpaRepository.deleteById(bookId);
    }

    @Override
    public Optional<Book> findById(Long bookId) {
        return bookJpaRepository.findById(bookId)
                .map(bookPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Book> findByIdForUpdate(Long bookId) {
        return bookJpaRepository.findByIdForUpdate(bookId)
                .map(bookPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return bookJpaRepository.existsById(id);
    }

    @Override
    public boolean existsByIsbnAndIdNot(String isbn, Long id) {
        if (id == null) {
            return bookJpaRepository.existsByIsbn(isbn);
        }
        return bookJpaRepository.existsByIsbnAndIdNot(isbn, id);
    }

    @Override
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