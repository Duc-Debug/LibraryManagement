package org.example.librarymanagement.infrastructure.persistence.book;

import java.util.List;
import java.util.Optional;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.port.inbound.common.PageResult;
import org.example.librarymanagement.port.outbound.book.BookRepository;
import org.example.librarymanagement.port.outbound.book.FindBookPort;
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
public class BookPersistenceAdapter implements 
        LoadBookPort, 
        CheckActiveBorrowPort, 
        SaveBookPort,
        FindBookPort,
        BookRepository {

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
        try {
            BookJpaEntity entity;

            // 1. Trường hợp TẠO MỚI (bookId == null): Tạo mới hoàn toàn JPA Entity
            if (book.getBookId() == null) {
                entity = mapper.toJpaEntity(book);
            } 
            // 2. Trường hợp CẬP NHẬT (bookId != null): Tìm Entity cũ từ DB và cập nhật thông tin
            else {
                entity = repository.findById(book.getBookId())
                        .orElseGet(() -> mapper.toJpaEntity(book));
                mapper.updateJpaEntity(book, entity);
            }

            BookJpaEntity saved = repository.save(entity);
            return mapper.toDomain(saved);
        } catch (DataIntegrityViolationException e) {
            // Bắt lỗi an toàn khi ISBN trùng lặp từ DB hoặc vi phạm ràng buộc
            throw new DomainException("Không thể lưu sách: ISBN đã tồn tại hoặc vi phạm ràng buộc dữ liệu.");
        }
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
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
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
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public boolean existsByIsbnAndIdNot(String isbn, Long id) {
        return repository.existsByIsbnAndIdNot(isbn, id);
    }

    @Override
    public List<Book> findAll(int page, int size) {
        return repository.findAll(PageRequest.of(page, size))
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    // Backup hàm findAll không phân trang
    public List<Book> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}