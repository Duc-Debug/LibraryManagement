package org.example.librarymanagement.infrastructure.persistence.book;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.exceptions.DuplicateResourceException;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.outbound.book.BookRepositoryPort;
import org.example.librarymanagement.port.outbound.book.LoadBookPort;
import org.example.librarymanagement.port.outbound.book.SaveBookPort;
import org.example.librarymanagement.port.outbound.borrow.CheckActiveBorrowPort;
import org.springframework.dao.DataIntegrityViolationException;
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
            BookPersistenceMapper bookPersistenceMapper) {
        this.bookJpaRepository = Objects.requireNonNull(bookJpaRepository, "BookJpaRepository must not be null");
        this.bookPersistenceMapper = Objects.requireNonNull(bookPersistenceMapper,
                "BookPersistenceMapper must not be null");
    }

    // ==================== CHECK ACTIVE BORROW PORT ====================

    @Override
    public boolean hasActiveBorrowSlips(Long bookId) {
        return bookJpaRepository.existsActiveBorrowByBookId(bookId) > 0;
    }

    // ==================== SAVE BOOK PORT ====================

    @Override
    public Book save(Book book) {
        BookJpaEntity entity;

        if (book.getId() == null) {
            entity = create(book);
        } else {
            entity = update(book);
        }

        try {
            // 2. Dùng saveAndFlush để đẩy SQL xuống DB ngay lập tức, đảm bảo bắt được constraint violation trong try-catch
            BookJpaEntity saved = bookJpaRepository.saveAndFlush(entity);
            return bookPersistenceMapper.toDomain(saved);
        } catch (DataIntegrityViolationException e) {
            // 1 & 3. Chỉ translate khi xác định đúng ISBN constraint, và dùng cùng DuplicateResourceException contract
            if (isIsbnConstraintViolation(e, book)) {
                throw new DuplicateResourceException("Book with ISBN '" + book.getIsbn() + "' already exists.");
            }
            // Nếu là lỗi FK / NOT NULL / constraint khác, ném lại nguyên vẹn
            throw e;
        }
    }

    private boolean isIsbnConstraintViolation(DataIntegrityViolationException e, Book book) {
        String msg = e.getMessage();
        Throwable rootCause = e.getRootCause();
        String rootMsg = rootCause != null ? rootCause.getMessage() : "";

        String combined = ((msg != null ? msg : "") + " " + (rootMsg != null ? rootMsg : "")).toLowerCase();
        if (combined.contains("isbn") || combined.contains("uk_books_isbn")) {
            return true;
        }

        if (book.getIsbn() != null && !book.getIsbn().isBlank()) {
            return existsByIsbnAndIdNot(book.getIsbn(), book.getId());
        }

        return false;
    }

    private BookJpaEntity create(Book book) {
        return bookPersistenceMapper.toJpaEntity(book);
    }

    private BookJpaEntity update(Book book) {
        BookJpaEntity entity = bookJpaRepository.findById(book.getId())
                .orElseThrow(() -> new org.example.librarymanagement.domain.exceptions.book.BookNotFoundException(
                        "Book not found with ID: " + book.getId()));

        bookPersistenceMapper.updateJpaEntity(book, entity);
        return entity;
    }

    @Override
    public void deleteById(Long bookId) {
        bookJpaRepository.deleteById(bookId);
    }

    // ==================== LOAD BOOK PORT ====================

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
    public boolean existsByIsbn(String isbn) {
        return bookJpaRepository.existsByIsbn(isbn);
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
            jpaPage = bookJpaRepository
                    .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCase(
                            search, search, search, pageable);
        }

        List<Book> domainBooks = jpaPage.getContent().stream()
                .map(bookPersistenceMapper::toDomain)
                .toList();

        return new PageResult<>(
                domainBooks,
                jpaPage.getNumber(),
                jpaPage.getSize(),
                jpaPage.getTotalElements(),
                jpaPage.getTotalPages());
    }

    @Override
    public List<Book> findAll(int page, int size) {
        // 4. Áp dụng cùng stable sort (id DESC) như các hàm phân trang khác
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return bookJpaRepository.findAll(pageable)
                .stream()
                .map(bookPersistenceMapper::toDomain)
                .filter(Objects::nonNull)
                .toList();
    }
}