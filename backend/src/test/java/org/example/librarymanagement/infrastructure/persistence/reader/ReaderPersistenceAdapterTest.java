package org.example.librarymanagement.infrastructure.persistence.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.example.librarymanagement.domain.entity.Reader;
import org.example.librarymanagement.domain.enums.CardStatus;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

class ReaderPersistenceAdapterTest {

    private ReaderJpaRepository readerJpaRepository;
    private ReaderPersistenceMapper readerPersistenceMapper;
    private ReaderPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        readerJpaRepository = mock(ReaderJpaRepository.class);
        readerPersistenceMapper = mock(ReaderPersistenceMapper.class);
        adapter = new ReaderPersistenceAdapter(readerJpaRepository, readerPersistenceMapper);
    }

    @Test
    @DisplayName("Save maps and persists reader")
    void save_MapsAndPersistsReader() {
        Reader domainReader = reader("RD-100", "Test");
        ReaderJpaEntity jpaEntity = new ReaderJpaEntity();

        when(readerPersistenceMapper.toJpaEntity(any())).thenReturn(jpaEntity);
        when(readerJpaRepository.save(any())).thenReturn(jpaEntity);
        when(readerPersistenceMapper.toDomain(any())).thenReturn(domainReader);

        Reader saved = adapter.save(domainReader);

        assertNotNull(saved);
        assertEquals("RD-100", saved.getCardNumber());
    }

    @Test
    @DisplayName("Find by card number returns matching optional")
    void findByCardNumber_ReturnsOptional() {
        ReaderJpaEntity entity = new ReaderJpaEntity();
        Reader domain = reader("RD-200", "Alice");

        when(readerJpaRepository.findByCardNumber("RD-200")).thenReturn(Optional.of(entity));
        when(readerPersistenceMapper.toDomain(entity)).thenReturn(domain);

        Optional<Reader> result = adapter.findByCardNumber("RD-200");

        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getName());
    }

    @Test
    @DisplayName("Find all readers uses active reader query")
    void findAll_UsesActiveReaderQuery() {
        ReaderJpaEntity entity = new ReaderJpaEntity();
        Reader domain = reader("RD-201", "Active Reader");

        when(readerJpaRepository.findByIsActiveTrue()).thenReturn(List.of(entity));
        when(readerPersistenceMapper.toDomain(entity)).thenReturn(domain);

        List<Reader> result = adapter.findAll();

        assertEquals(1, result.size());
        assertEquals("RD-201", result.get(0).getCardNumber());
        verify(readerJpaRepository).findByIsActiveTrue();
    }

    @Test
    @DisplayName("Find all readers with pagination uses active reader query")
    void findAll_PaginatedUsesActiveReaderQuery() {
        ReaderJpaEntity entity = new ReaderJpaEntity();
        Reader domain = reader("RD-202", "Active Reader");
        PageImpl<ReaderJpaEntity> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1);

        when(readerJpaRepository.findAll(
                anyReaderSpecification(),
                eq(PageRequest.of(0, 10)))).thenReturn(page);
        when(readerPersistenceMapper.toDomain(entity)).thenReturn(domain);

        PageResult<Reader> pageResult = adapter.findAll(0, 10);

        assertEquals(1, pageResult.content().size());
        assertEquals(1, pageResult.totalElements());
        assertEquals("RD-202", pageResult.content().get(0).getCardNumber());
        verify(readerJpaRepository).findAll(
                anyReaderSpecification(),
                eq(PageRequest.of(0, 10)));
    }

    @Test
    @DisplayName("Find readers by creator uses active reader query")
    void findByCreatedByUserId_UsesActiveReaderQuery() {
        ReaderJpaEntity entity = new ReaderJpaEntity();
        Reader domain = reader("RD-203", "Active Reader");

        when(readerJpaRepository.findByCreatedByUserIdAndIsActiveTrue(1L)).thenReturn(List.of(entity));
        when(readerPersistenceMapper.toDomain(entity)).thenReturn(domain);

        List<Reader> result = adapter.findByCreatedByUserId(1L);

        assertEquals(1, result.size());
        assertEquals("RD-203", result.get(0).getCardNumber());
        verify(readerJpaRepository).findByCreatedByUserIdAndIsActiveTrue(1L);
    }

    @Test
    @DisplayName("Find Reader by creator with pagination uses active reader query")
    void findByCreatedByUserId_PaginatedUsesActiveReaderQuery() {
        ReaderJpaEntity entity = new ReaderJpaEntity();
        Reader domain = reader("RD-300", "Bob");
        PageImpl<ReaderJpaEntity> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1);

        when(readerJpaRepository.findAll(
                anyReaderSpecification(),
                eq(PageRequest.of(0, 10)))).thenReturn(page);
        when(readerPersistenceMapper.toDomain(entity)).thenReturn(domain);

        PageResult<Reader> pageResult = adapter.findByCreatedByUserId(1L, 0, 10);

        assertEquals(1, pageResult.content().size());
        assertEquals("RD-300", pageResult.content().get(0).getCardNumber());
        verify(readerJpaRepository).findAll(
                anyReaderSpecification(),
                eq(PageRequest.of(0, 10)));
    }

    @SuppressWarnings("unchecked")
    private Specification<ReaderJpaEntity> anyReaderSpecification() {
        return any(Specification.class);
    }

    private Reader reader(String cardNumber, String name) {
        return Reader.builder()
                .cardNumber(cardNumber)
                .name(name)
                .email(cardNumber.toLowerCase() + "@test.com")
                .phoneNumber("0912345678")
                .address("HN")
                .cardStatus(CardStatus.ACTIVE)
                .build();
    }
}
