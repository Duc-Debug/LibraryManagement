package org.example.librarymanagement.infrastructure.persistence.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.example.librarymanagement.LibraryManagementApplication;
import org.example.librarymanagement.domain.entity.Reader;
import org.example.librarymanagement.domain.enums.CardStatus;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.outbound.reader.ReaderRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = LibraryManagementApplication.class)
@ActiveProfiles("test")
@Transactional
class ReaderPersistenceIntegrationTest {

    @Autowired
    private ReaderRepositoryPort readerRepositoryPort;

    @Test
    void findAllExcludesInactiveReaders() {
        Reader activeReader = reader("RD-ACTIVE-LIST", "active.list@test.com", "0912345601", 10L, true);
        Reader inactiveReader = reader("RD-INACTIVE-LIST", "inactive.list@test.com", "0912345602", 10L, false);

        readerRepositoryPort.save(activeReader);
        readerRepositoryPort.save(inactiveReader);

        List<Reader> readers = readerRepositoryPort.findAll();

        assertTrue(containsCardNumber(readers, "RD-ACTIVE-LIST"));
        assertFalse(containsCardNumber(readers, "RD-INACTIVE-LIST"));
    }

    @Test
    void findAllPaginatedExcludesInactiveReaderAndUsesActiveTotal() {
        Reader activeReader = reader("RD-ACTIVE-PAGE", "active.page@test.com", "0912345603", 20L, true);
        Reader inactiveReader = reader("RD-INACTIVE-PAGE", "inactive.page@test.com", "0912345604", 20L, false);

        readerRepositoryPort.save(activeReader);
        readerRepositoryPort.save(inactiveReader);

        PageResult<Reader> page = readerRepositoryPort.findAll(0, 10);

        assertEquals(1, page.totalElements());
        assertEquals(1, page.content().size());
        assertEquals("RD-ACTIVE-PAGE", page.content().get(0).getCardNumber());
    }

    @Test
    void findByCreatedByUserIdExcludesInactiveReaders() {
        Reader activeOwnedReader = reader("RD-ACTIVE-OWNER", "active.owner@test.com", "0912345605", 30L, true);
        Reader inactiveOwnedReader = reader("RD-INACTIVE-OWNER", "inactive.owner@test.com", "0912345606", 30L, false);
        Reader otherReader = reader("RD-OTHER-OWNER", "other.owner@test.com", "0912345607", 31L, true);

        readerRepositoryPort.save(activeOwnedReader);
        readerRepositoryPort.save(inactiveOwnedReader);
        readerRepositoryPort.save(otherReader);

        List<Reader> readers = readerRepositoryPort.findByCreatedByUserId(30L);

        assertEquals(1, readers.size());
        assertEquals("RD-ACTIVE-OWNER", readers.get(0).getCardNumber());
    }

    private boolean containsCardNumber(List<Reader> readers, String cardNumber) {
        return readers.stream()
                .anyMatch(reader -> cardNumber.equals(reader.getCardNumber()));
    }

    private Reader reader(
            String cardNumber,
            String email,
            String phoneNumber,
            Long createdByUserId,
            boolean active
    ) {
        LocalDate issuedAt = LocalDate.of(2026, 1, 1);
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 1, 8, 0);

        return Reader.builder()
                .cardNumber(cardNumber)
                .name("Reader " + cardNumber)
                .email(email)
                .phoneNumber(phoneNumber)
                .address("Ha Noi")
                .cardStatus(CardStatus.ACTIVE)
                .cardIssuedAt(issuedAt)
                .cardExpiryAt(issuedAt.plusYears(1))
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .isActive(active)
                .createdByUserId(createdByUserId)
                .build();
    }
}
