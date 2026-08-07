package org.example.librarymanagement.infrastructure.persistence.reader;

import java.util.List;
import java.util.Optional;

import org.example.librarymanagement.domain.entity.Readers;
import org.example.librarymanagement.domain.enums.CardStatus;
import org.example.librarymanagement.port.dtos.common.PageResult;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
    @DisplayName("Lưu bạn đọc và chuyển đổi đối tượng Domain chính xác")
    void save_MapsAndPersistsReader() {
        Readers domainReader = Readers.builder().cardNumber("RD-100").name("Test").email("t@g.com").phoneNumber("0123").address("HN").cardStatus(CardStatus.ACTIVE).build();
        ReaderJpaEntity jpaEntity = new ReaderJpaEntity();

        when(readerPersistenceMapper.toJpaEntity(any())).thenReturn(jpaEntity);
        when(readerJpaRepository.save(any())).thenReturn(jpaEntity);
        when(readerPersistenceMapper.toDomain(any())).thenReturn(domainReader);

        Readers saved = adapter.save(domainReader);

        assertNotNull(saved);
        assertEquals("RD-100", saved.getCardNumber());
    }

    @Test
    @DisplayName("Truy vấn theo Mã thẻ trả về Optional tương ứng")
    void findByCardNumber_ReturnsOptional() {
        ReaderJpaEntity entity = new ReaderJpaEntity();
        Readers domain = Readers.builder().cardNumber("RD-200").name("Alice").email("alice@gmail.com").phoneNumber("0912345678").address("HN").cardStatus(CardStatus.ACTIVE).build();

        when(readerJpaRepository.findByCardNumber("RD-200")).thenReturn(Optional.of(entity));
        when(readerPersistenceMapper.toDomain(entity)).thenReturn(domain);

        Optional<Readers> result = adapter.findByCardNumber("RD-200");

        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getName());
    }

    @Test
    @DisplayName("Phân trang bạn đọc theo Thủ thư tạo chính xác")
    void findAll_Paginated() {
        ReaderJpaEntity entity = new ReaderJpaEntity();
        Readers domain = Readers.builder().cardNumber("RD-300").name("Bob").email("bob@gmail.com").phoneNumber("0987654321").address("HN").cardStatus(CardStatus.ACTIVE).build();
        PageImpl<ReaderJpaEntity> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1);

        when(readerJpaRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);
        when(readerPersistenceMapper.toDomain(entity)).thenReturn(domain);

        PageResult<Readers> pageResult = adapter.findAll(0, 10);

        assertEquals(1, pageResult.content().size());
        assertEquals("RD-300", pageResult.content().get(0).getCardNumber());
    }
}
