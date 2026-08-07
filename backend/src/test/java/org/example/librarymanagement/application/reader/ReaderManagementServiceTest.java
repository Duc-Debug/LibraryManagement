package org.example.librarymanagement.application.reader;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.example.librarymanagement.domain.entity.Reader;
import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.enums.CardStatus;
import org.example.librarymanagement.domain.exceptions.reader.ReaderAccessDeniedException;
import org.example.librarymanagement.domain.exceptions.reader.ReaderAlreadyExistsException;
import org.example.librarymanagement.domain.exceptions.reader.ReaderHasActiveBorrowException;
import org.example.librarymanagement.domain.exceptions.reader.ReaderNotFoundException;
import org.example.librarymanagement.domain.exceptions.shared.UnauthenticatedException;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.dtos.reader.CreateReaderCommand;
import org.example.librarymanagement.port.dtos.reader.ReaderResult;
import org.example.librarymanagement.port.dtos.reader.UpdateReaderCommand;
import org.example.librarymanagement.port.dtos.reader.ChangeCardStatusCommand;
import org.example.librarymanagement.port.outbound.borrow.CheckActiveReaderBorrowPort;
import org.example.librarymanagement.port.outbound.manage.FindUserPort;
import org.example.librarymanagement.port.outbound.manage.GetAuthenticatedUserPort;
import org.example.librarymanagement.port.outbound.reader.CardNumberGeneratorPort;
import org.example.librarymanagement.port.outbound.reader.ReaderRepositoryPort;
import org.example.librarymanagement.port.outbound.reader.ReaderSearchCriteria;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private CheckActiveReaderBorrowPort checkActiveReaderBorrowPort;

    private ReaderManagementService readerManagementService;

    @BeforeEach
    void setUp() {
        readerRepositoryPort =
                mock(ReaderRepositoryPort.class);

        getAuthenticatedUserPort =
                mock(GetAuthenticatedUserPort.class);

        cardNumberGeneratorPort =
                mock(CardNumberGeneratorPort.class);

        findUserPort =
                mock(FindUserPort.class);

        checkActiveReaderBorrowPort =
                mock(CheckActiveReaderBorrowPort.class);

        readerManagementService =
                new ReaderManagementService(
                        readerRepositoryPort,
                        getAuthenticatedUserPort,
                        cardNumberGeneratorPort,
                        findUserPort,
                        checkActiveReaderBorrowPort
                );
    }

    @Test
    @DisplayName(
            "Creates reader successfully when request is valid"
    )
    void createReader_Success() {
        User librarian = mockUser(
                1L,
                "librarian1",
                "LIBRARIAN"
        );

        when(getAuthenticatedUserPort.getCurrentUser())
                .thenReturn(librarian);

        when(readerRepositoryPort.existsByEmail(
                anyString()
        )).thenReturn(false);

        when(readerRepositoryPort.existsByPhoneNumber(
                anyString()
        )).thenReturn(false);

        when(cardNumberGeneratorPort.generateNextCardNumber())
                .thenReturn("RD-260805-1001");

        when(readerRepositoryPort.save(any()))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        CreateReaderCommand command =
                new CreateReaderCommand(
                        "Nguyen Van A",
                        "nva@gmail.com",
                        "0987654321",
                        "Ha Noi"
                );

        ReaderResult result =
                readerManagementService.createReader(command);

        assertNotNull(result);
        assertEquals(
                "RD-260805-1001",
                result.cardNumber()
        );
        assertEquals(
                "Nguyen Van A",
                result.name()
        );

        verify(readerRepositoryPort).save(any());
    }

    @Test
    @DisplayName(
            "Throws UnauthenticatedException when user is not authenticated"
    )
    void createReader_ThrowsUnauthenticated_WhenUserIsNull() {
        when(getAuthenticatedUserPort.getCurrentUser())
                .thenReturn(null);

        CreateReaderCommand command =
                new CreateReaderCommand(
                        "Nguyen Van A",
                        "nva@gmail.com",
                        "0987654321",
                        "Ha Noi"
                );

        assertThrows(
                UnauthenticatedException.class,
                () -> readerManagementService
                        .createReader(command)
        );
    }

    @Test
    @DisplayName(
            "Throws ReaderAlreadyExistsException when email already exists"
    )
    void createReader_ThrowsDuplicateEmail() {
        User librarian = mockUser(
                1L,
                "librarian1",
                "LIBRARIAN"
        );

        when(getAuthenticatedUserPort.getCurrentUser())
                .thenReturn(librarian);

        when(readerRepositoryPort.existsByEmail(
                "duplicate@gmail.com"
        )).thenReturn(true);

        CreateReaderCommand command =
                new CreateReaderCommand(
                        "Nguyen Van A",
                        "duplicate@gmail.com",
                        "0987654321",
                        "Ha Noi"
                );

        assertThrows(
                ReaderAlreadyExistsException.class,
                () -> readerManagementService
                        .createReader(command)
        );
    }

    @Test
    @DisplayName(
            "Returns all readers for administrator"
    )
    void getAllReaders_Admin_ReturnsAllReaders() {
        User admin = mockUser(
                1L,
                "admin",
                "ADMIN"
        );

        when(getAuthenticatedUserPort.getCurrentUser())
                .thenReturn(admin);

        Reader reader = existingReader(
                1L,
                2L,
                true
        );

        when(readerRepositoryPort.search(any(ReaderSearchCriteria.class)))
                .thenReturn(
                        PageResult.of(
                                List.of(reader),
                                0,
                                10,
                                1
                        )
                );

        PageResult<ReaderResult> result =
                readerManagementService
                        .getAllReaders(0, 10);

        assertEquals(
                1,
                result.content().size()
        );

        ArgumentCaptor<ReaderSearchCriteria> criteriaCaptor =
                ArgumentCaptor.forClass(ReaderSearchCriteria.class);

        verify(readerRepositoryPort)
                .search(criteriaCaptor.capture());

        ReaderSearchCriteria criteria = criteriaCaptor.getValue();
        assertNull(criteria.createdByUserId());
        assertNull(criteria.keyword());
        assertNull(criteria.status());
        assertEquals(0, criteria.page());
        assertEquals(10, criteria.size());
    }

    @Test
    @DisplayName(
            "Throws UnauthenticatedException before validating pagination"
    )
    void getAllReaders_UnauthenticatedWithInvalidPage_ThrowsUnauthenticated() {
        when(getAuthenticatedUserPort.getCurrentUser())
                .thenReturn(null);

        assertThrows(
                UnauthenticatedException.class,
                () -> readerManagementService
                        .getAllReaders(-1, 10)
        );

        verify(readerRepositoryPort, never())
                .search(any(ReaderSearchCriteria.class));
    }

    @Test
    @DisplayName(
            "Throws IllegalArgumentException for invalid page after authentication"
    )
    void getAllReaders_AuthenticatedWithInvalidPage_ThrowsValidationError() {
        User admin = mockUser(
                1L,
                "admin",
                "ADMIN"
        );

        when(getAuthenticatedUserPort.getCurrentUser())
                .thenReturn(admin);

        assertThrows(
                IllegalArgumentException.class,
                () -> readerManagementService
                        .getAllReaders(-1, 10)
        );

        verify(readerRepositoryPort, never())
                .search(any(ReaderSearchCriteria.class));
    }

    @Test
    @DisplayName(
            "Updates reader successfully when librarian owns the reader"
    )
    void updateReader_Success_WhenLibrarianOwnsReader() {
        User librarian = mockUser(
                1L,
                "librarian",
                "LIBRARIAN"
        );

        Reader existingReader =
                existingReader(
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
                .thenReturn(
                        Optional.of(existingReader)
                );

        when(readerRepositoryPort
                .existsByEmailAndIdNot(
                        "new.email@gmail.com",
                        10L
                ))
                .thenReturn(false);

        when(readerRepositoryPort
                .existsByPhoneNumberAndIdNot(
                        "0987654321",
                        10L
                ))
                .thenReturn(false);

        when(readerRepositoryPort.save(existingReader))
                .thenReturn(existingReader);

        ReaderResult result =
                readerManagementService
                        .updateReader(command);

        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals(
                "Nguyen Van B",
                result.name()
        );
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
    @DisplayName(
            "Throws ReaderNotFoundException when updating missing reader"
    )
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
                () -> readerManagementService
                        .updateReader(command)
        );

        verify(readerRepositoryPort, never())
                .save(any());
    }

    @Test
    @DisplayName(
            "Throws ReaderAccessDeniedException when librarian does not own reader"
    )
    void updateReader_ThrowsAccessDenied_WhenNotOwner() {
        User librarian = mockUser(
                1L,
                "librarian",
                "LIBRARIAN"
        );

        Reader existingReader =
                existingReader(
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
                .thenReturn(
                        Optional.of(existingReader)
                );

        assertThrows(
                ReaderAccessDeniedException.class,
                () -> readerManagementService
                        .updateReader(command)
        );

        verify(readerRepositoryPort, never())
                .existsByEmailAndIdNot(
                        anyString(),
                        anyLong()
                );

        verify(readerRepositoryPort, never())
                .save(any());
    }

    @Test
    @DisplayName(
            "Throws ReaderAlreadyExistsException when update email is duplicated"
    )
    void updateReader_ThrowsDuplicateEmail() {
        User librarian = mockUser(
                1L,
                "librarian",
                "LIBRARIAN"
        );

        Reader existingReader =
                existingReader(
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
                .thenReturn(
                        Optional.of(existingReader)
                );

        when(readerRepositoryPort
                .existsByEmailAndIdNot(
                        "duplicate@gmail.com",
                        10L
                ))
                .thenReturn(true);

        assertThrows(
                ReaderAlreadyExistsException.class,
                () -> readerManagementService
                        .updateReader(command)
        );

        verify(readerRepositoryPort, never())
                .save(any());
    }

    @Test
    @DisplayName(
            "Deactivates reader successfully when reader has no active borrow"
    )
    void deleteReader_Success() {
        User librarian = mockUser(
                1L,
                "librarian",
                "LIBRARIAN"
        );

        Reader reader = existingReader(
                10L,
                1L,
                true
        );

        when(getAuthenticatedUserPort.getCurrentUser())
                .thenReturn(librarian);

        when(readerRepositoryPort.findById(10L))
                .thenReturn(Optional.of(reader));

        when(checkActiveReaderBorrowPort
                .hasActiveBorrowByReaderId(10L))
                .thenReturn(false);

        when(readerRepositoryPort.save(reader))
                .thenReturn(reader);

        readerManagementService.deleteReader(10L);

        assertFalse(reader.isActive());

        verify(checkActiveReaderBorrowPort)
                .hasActiveBorrowByReaderId(10L);

        verify(readerRepositoryPort)
                .save(reader);
    }

    @Test
    @DisplayName(
            "Throws ReaderHasActiveBorrowException when reader is borrowing books"
    )
    void deleteReader_ThrowsActiveBorrow() {
        User librarian = mockUser(
                1L,
                "librarian",
                "LIBRARIAN"
        );

        Reader reader = existingReader(
                10L,
                1L,
                true
        );

        when(getAuthenticatedUserPort.getCurrentUser())
                .thenReturn(librarian);

        when(readerRepositoryPort.findById(10L))
                .thenReturn(Optional.of(reader));

        when(checkActiveReaderBorrowPort
                .hasActiveBorrowByReaderId(10L))
                .thenReturn(true);

        assertThrows(
                ReaderHasActiveBorrowException.class,
                () -> readerManagementService
                        .deleteReader(10L)
        );

        verify(readerRepositoryPort, never())
                .save(any());
    }

    @Test
    @DisplayName(
            "Throws ReaderNotFoundException when deleting missing reader"
    )
    void deleteReader_ThrowsNotFound() {
        User librarian = mockUser(
                1L,
                "librarian",
                "LIBRARIAN"
        );

        when(getAuthenticatedUserPort.getCurrentUser())
                .thenReturn(librarian);

        when(readerRepositoryPort.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ReaderNotFoundException.class,
                () -> readerManagementService
                        .deleteReader(99L)
        );

        verify(checkActiveReaderBorrowPort, never())
                .hasActiveBorrowByReaderId(anyLong());

        verify(readerRepositoryPort, never())
                .save(any());
    }

    @Test
    @DisplayName(
            "Throws ReaderAccessDeniedException when deleting another librarian reader"
    )
    void deleteReader_ThrowsAccessDenied() {
        User librarian = mockUser(
                1L,
                "librarian",
                "LIBRARIAN"
        );

        Reader reader = existingReader(
                10L,
                2L,
                true
        );

        when(getAuthenticatedUserPort.getCurrentUser())
                .thenReturn(librarian);

        when(readerRepositoryPort.findById(10L))
                .thenReturn(Optional.of(reader));

        assertThrows(
                ReaderAccessDeniedException.class,
                () -> readerManagementService
                        .deleteReader(10L)
        );

        verify(checkActiveReaderBorrowPort, never())
                .hasActiveBorrowByReaderId(anyLong());

        verify(readerRepositoryPort, never())
                .save(any());
    }

    private Reader existingReader(
            Long id,
            Long creatorId,
            boolean active
    ) {
        LocalDate issuedAt =
                LocalDate.of(
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

        return Reader.builder()
                .id(id)
                .cardNumber("RD-2026-" + id)
                .name("Original Reader")
                .email("reader" + id + "@gmail.com")
                .phoneNumber("0912345678")
                .address("Ha Noi")
                .cardStatus(CardStatus.ACTIVE)
                .cardIssuedAt(issuedAt)
                .cardExpiryAt(
                        issuedAt.plusYears(1)
                )
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .isActive(active)
                .createdByUserId(creatorId)
                .build();
    }

    private User mockUser(
            Long id,
            String username,
            String roleName
    ) {
        return new User(
                id,
                username,
                "hash",
                "Full " + username,
                username + "@test.com",
                "0123",
                true,
                null,
                null,
                null,
                Set.of(
                        new Role(
                                1L,
                                roleName,
                                roleName
                        )
                )
        );
    }

    @Test
    @DisplayName("changeCardStatus - Success when owner or admin")
    void givenValidCommandAndAuthorizedUser_whenChangeCardStatus_thenReturnUpdatedResult() {
        // Arrange
        Long readerId = 1L;
        Long creatorId = 10L;
        User mockOwner = mockUser(creatorId, "owner", "LIBRARIAN");
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(mockOwner);

        Reader existingReader = existingReader(readerId, creatorId, true);
        when(readerRepositoryPort.findById(readerId)).thenReturn(Optional.of(existingReader));
        when(readerRepositoryPort.save(any(Reader.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChangeCardStatusCommand command = new ChangeCardStatusCommand(readerId, CardStatus.LOCKED);

        // Act
        ReaderResult result = readerManagementService.changeCardStatus(command);

        // Assert
        assertNotNull(result);
        assertEquals(CardStatus.LOCKED, result.cardStatus());
        verify(readerRepositoryPort).save(existingReader);
    }

    @Test
    @DisplayName("changeCardStatus - Throw exception when access denied")
    void givenUnauthorisedUser_whenChangeCardStatus_thenThrowReaderAccessDeniedException() {
        // Arrange
        Long readerId = 1L;
        Long creatorId = 10L;
        User mockUser = mockUser(99L, "unauthorized", "LIBRARIAN");
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(mockUser);

        Reader existingReader = existingReader(readerId, creatorId, true);
        when(readerRepositoryPort.findById(readerId)).thenReturn(Optional.of(existingReader));

        ChangeCardStatusCommand command = new ChangeCardStatusCommand(readerId, CardStatus.LOCKED);

        // Act & Assert
        assertThrows(ReaderAccessDeniedException.class, () -> {
            readerManagementService.changeCardStatus(command);
        });
    }

    @Test
    @DisplayName("changeCardStatus - Throw exception when reader not found")
    void givenNonExistentReaderId_whenChangeCardStatus_thenThrowReaderNotFoundException() {
        // Arrange
        Long readerId = 99L;
        User mockUser = mockUser(1L, "admin", "ADMIN");
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(mockUser);

        when(readerRepositoryPort.findById(readerId)).thenReturn(Optional.empty());

        ChangeCardStatusCommand command = new ChangeCardStatusCommand(readerId, CardStatus.LOCKED);

        // Act & Assert
        assertThrows(ReaderNotFoundException.class, () -> {
            readerManagementService.changeCardStatus(command);
        });
    }
}
