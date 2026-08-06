package org.example.librarymanagement.application.reader;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.example.librarymanagement.domain.entity.Readers;
import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.enums.CardStatus;
import org.example.librarymanagement.domain.exceptions.ReaderAccessDeniedException;
import org.example.librarymanagement.domain.exceptions.ReaderAlreadyExistsException;
import org.example.librarymanagement.domain.exceptions.ReaderNotFoundException;
import org.example.librarymanagement.domain.exceptions.UnauthenticatedException;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.dtos.reader.CreateReaderCommand;
import org.example.librarymanagement.port.dtos.reader.ReaderResult;
import org.example.librarymanagement.port.dtos.reader.UpdateReaderCommand;
import org.example.librarymanagement.port.outbound.manage.FindUserPort;
import org.example.librarymanagement.port.outbound.manage.GetAuthenticatedUserPort;
import org.example.librarymanagement.port.outbound.reader.CardNumberGeneratorPort;
import org.example.librarymanagement.port.outbound.reader.ReaderRepositoryPort;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReaderManagementServiceTest {

    private ReaderRepositoryPort readerRepositoryPort;
    private GetAuthenticatedUserPort getAuthenticatedUserPort;
    private CardNumberGeneratorPort cardNumberGeneratorPort;
    private FindUserPort findUserPort;

    private ReaderManagementService readerManagementService;

    @BeforeEach
    void setUp() {
        readerRepositoryPort = mock(ReaderRepositoryPort.class);
        getAuthenticatedUserPort = mock(GetAuthenticatedUserPort.class);
        cardNumberGeneratorPort = mock(CardNumberGeneratorPort.class);
        findUserPort = mock(FindUserPort.class);

        readerManagementService = new ReaderManagementService(
                readerRepositoryPort,
                getAuthenticatedUserPort,
                cardNumberGeneratorPort,
                findUserPort
        );
    }

    @Test
    @DisplayName("Tạo bạn đọc thành công khi dữ liệu hợp lệ và đã đăng nhập")
    void createReader_Success() {
        User librarian = mockUser(1L, "thuthu1", "LIBRARIAN");
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(librarian);
        when(readerRepositoryPort.existsByEmail(anyString())).thenReturn(false);
        when(readerRepositoryPort.existsByPhoneNumber(anyString())).thenReturn(false);
        when(cardNumberGeneratorPort.generateNextCardNumber()).thenReturn("RD-260805-1001");
        when(readerRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateReaderCommand command = new CreateReaderCommand("Nguyễn Văn A", "nva@gmail.com", "0987654321", "Hà Nội");

        ReaderResult result = readerManagementService.createReader(command);

        assertNotNull(result);
        assertEquals("RD-260805-1001", result.cardNumber());
        assertEquals("Nguyễn Văn A", result.name());
        verify(readerRepositoryPort).save(any());
    }

    @Test
    @DisplayName("Ném lỗi UnauthenticatedException khi chưa đăng nhập (currentUser == null)")
    void createReader_ThrowsUnauthenticated_WhenUserIsNull() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(null);

        CreateReaderCommand command = new CreateReaderCommand("Nguyễn Văn A", "nva@gmail.com", "0987654321", "Hà Nội");

        assertThrows(UnauthenticatedException.class, () -> readerManagementService.createReader(command));
    }

    @Test
    @DisplayName("Ném lỗi ReaderAlreadyExistsException khi trùng Email")
    void createReader_ThrowsDuplicateEmail() {
        User librarian = mockUser(1L, "thuthu1", "LIBRARIAN");
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(librarian);
        when(readerRepositoryPort.existsByEmail("duplicate@gmail.com")).thenReturn(true);

        CreateReaderCommand command = new CreateReaderCommand("Nguyễn Văn A", "duplicate@gmail.com", "0987654321", "Hà Nội");

        assertThrows(ReaderAlreadyExistsException.class, () -> readerManagementService.createReader(command));
    }

    @Test
    @DisplayName("Admin lấy danh sách phân trang được tất cả các độc giả trong hệ thống")
    void getAllReaders_Admin_ReturnsAllReaders() {
        User admin = mockUser(1L, "admin", "ADMIN");
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(admin);

        Readers r1 = mockReader(1L, "RD-1", "A", 2L);
        when(readerRepositoryPort.findAll(0, 10)).thenReturn(PageResult.of(List.of(r1), 0, 10, 1));

        PageResult<ReaderResult> result = readerManagementService.getAllReaders(0, 10);

        assertEquals(1, result.content().size());
        verify(readerRepositoryPort).findAll(0, 10);
    }
    @Test
@DisplayName("Updates reader successfully when librarian owns the reader")
void updateReader_Success_WhenLibrarianOwnsReader() {
    User librarian = mockUser(
            1L,
            "librarian",
            "LIBRARIAN"
    );

    Readers existingReader = existingReader(
            10L,
            1L,
            true
    );

    UpdateReaderCommand command =
            new UpdateReaderCommand(
                    10L,
                    "  Nguyen Van B  ",
                    "  NEW.EMAIL@GMAIL.COM  ",
                    "0987654321",
                    "  Thai Nguyen  "
            );

    when(getAuthenticatedUserPort.getCurrentUser())
            .thenReturn(librarian);

    when(readerRepositoryPort.findById(10L))
            .thenReturn(Optional.of(existingReader));

    when(readerRepositoryPort.existsByEmailAndIdNot(
            "new.email@gmail.com",
            10L
    )).thenReturn(false);

    when(readerRepositoryPort.existsByPhoneNumberAndIdNot(
            "0987654321",
            10L
    )).thenReturn(false);

    when(readerRepositoryPort.save(existingReader))
            .thenReturn(existingReader);

    ReaderResult result =
            readerManagementService.updateReader(command);

    assertNotNull(result);
    assertEquals(10L, result.id());
    assertEquals("Nguyen Van B", result.name());
    assertEquals(
            "new.email@gmail.com",
            result.email()
    );
    assertEquals(
            "0987654321",
            result.phoneNumber()
    );
    assertEquals(
            "Thai Nguyen",
            result.address()
    );

    verify(readerRepositoryPort)
            .existsByEmailAndIdNot(
                    "new.email@gmail.com",
                    10L
            );

    verify(readerRepositoryPort)
            .existsByPhoneNumberAndIdNot(
                    "0987654321",
                    10L
            );

    verify(readerRepositoryPort)
            .save(existingReader);
}
@Test
@DisplayName("Throws ReaderNotFoundException when reader does not exist")
void updateReader_ThrowsNotFound_WhenReaderDoesNotExist() {
    User librarian = mockUser(
            1L,
            "librarian",
            "LIBRARIAN"
    );

    UpdateReaderCommand command =
            new UpdateReaderCommand(
                    99L,
                    "Nguyen Van B",
                    "reader@gmail.com",
                    "0987654321",
                    "Thai Nguyen"
            );

    when(getAuthenticatedUserPort.getCurrentUser())
            .thenReturn(librarian);

    when(readerRepositoryPort.findById(99L))
            .thenReturn(Optional.empty());

    assertThrows(
            ReaderNotFoundException.class,
            () -> readerManagementService.updateReader(command)
    );

    verify(readerRepositoryPort, never())
            .save(any());
}
@Test
@DisplayName("Throws ReaderAccessDeniedException when librarian does not own the reader")
void updateReader_ThrowsAccessDenied_WhenLibrarianDoesNotOwnReader() {
    User librarian = mockUser(
            1L,
            "librarian",
            "LIBRARIAN"
    );

    Readers existingReader = existingReader(
            10L,
            2L,
            true
    );

    UpdateReaderCommand command =
            new UpdateReaderCommand(
                    10L,
                    "Nguyen Van B",
                    "reader@gmail.com",
                    "0987654321",
                    "Thai Nguyen"
            );

    when(getAuthenticatedUserPort.getCurrentUser())
            .thenReturn(librarian);

    when(readerRepositoryPort.findById(10L))
            .thenReturn(Optional.of(existingReader));

    assertThrows(
            ReaderAccessDeniedException.class,
            () -> readerManagementService.updateReader(command)
    );

    verify(readerRepositoryPort, never())
            .existsByEmailAndIdNot(
                    anyString(),
                    any()
            );

    verify(readerRepositoryPort, never())
            .save(any());
}
@Test
@DisplayName("Throws ReaderAlreadyExistsException when email belongs to another reader")
void updateReader_ThrowsDuplicateEmail() {
    User librarian = mockUser(
            1L,
            "librarian",
            "LIBRARIAN"
    );

    Readers existingReader = existingReader(
            10L,
            1L,
            true
    );

    UpdateReaderCommand command =
            new UpdateReaderCommand(
                    10L,
                    "Nguyen Van B",
                    "duplicate@gmail.com",
                    "0987654321",
                    "Thai Nguyen"
            );

    when(getAuthenticatedUserPort.getCurrentUser())
            .thenReturn(librarian);

    when(readerRepositoryPort.findById(10L))
            .thenReturn(Optional.of(existingReader));

    when(readerRepositoryPort.existsByEmailAndIdNot(
            "duplicate@gmail.com",
            10L
    )).thenReturn(true);

    assertThrows(
            ReaderAlreadyExistsException.class,
            () -> readerManagementService.updateReader(command)
    );

    verify(readerRepositoryPort, never())
            .save(any());
}
private Readers existingReader(
        Long id,
        Long creatorId,
        boolean active
) {
    LocalDate issuedAt = LocalDate.of(
            2026,
            1,
            1
    );

    LocalDateTime createdAt =
            LocalDateTime.of(
                    2026,
                    1,
                    1,
                    8,
                    0
            );

    return Readers.builder()
            .id(id)
            .cardNumber("RD-2026-0001")
            .name("Original Reader")
            .email("original@gmail.com")
            .phoneNumber("0912345678")
            .address("Ha Noi")
            .cardStatus(CardStatus.ACTIVE)
            .cardIssuedAt(issuedAt)
            .cardExpiryAt(issuedAt.plusYears(1))
            .createdAt(createdAt)
            .updatedAt(createdAt)
            .isActive(active)
            .createdByUserId(creatorId)
            .build();
}

    private User mockUser(Long id, String username, String roleName) {
        return new User(id, username, "hash", "Full " + username, username + "@test.com", "0123", true, null, null, null, Set.of(new Role(1L, roleName, roleName)));
    }

    private Readers mockReader(Long id, String cardCode, String name, Long creatorId) {
        return Readers.builder().id(id).cardNumber(cardCode).name(name).email("e@test.com").phoneNumber("0123").address("HN").createdByUserId(creatorId).build();
    }
}
