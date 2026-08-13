package org.example.librarymanagement.application.borrow;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.entity.BorrowSlip;
import org.example.librarymanagement.domain.entity.Reader;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.enums.CardStatus;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.domain.exceptions.reader.ReaderNotFoundException;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipResponseDto;
import org.example.librarymanagement.port.dtos.borrow.CreateBorrowSlipCommand;
import org.example.librarymanagement.port.inbound.borrow.CheckBorrowEligibilityUseCase;
import org.example.librarymanagement.port.outbound.book.BookRepositoryPort;
import org.example.librarymanagement.port.outbound.borrow.BorrowDetailsRepositoryPort;
import org.example.librarymanagement.port.outbound.borrow.SaveBorrowSlipPort;
import org.example.librarymanagement.port.outbound.reader.ReaderRepositoryPort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateBorrowSlipServiceTest {

    @Mock
    private CheckBorrowEligibilityUseCase checkBorrowEligibilityUseCase;

    @Mock
    private ReaderRepositoryPort readerRepositoryPort;

    @Mock
    private BookRepositoryPort bookRepositoryPort;

    @Mock
    private SaveBorrowSlipPort saveBorrowSlipPort;

    @Mock
    private BorrowDetailsRepositoryPort borrowDetailsRepositoryPort;

    @Mock
    private GetAuthenticatedUserPort getAuthenticatedUserPort;

    private CreateBorrowSlipService createBorrowSlipService;

    private User sampleStaff;
    private Reader sampleReader;
    private Book sampleBook;

    @BeforeEach
    void setUp() {
        createBorrowSlipService = new CreateBorrowSlipService(
                checkBorrowEligibilityUseCase,
                readerRepositoryPort,
                bookRepositoryPort,
                saveBorrowSlipPort,
                borrowDetailsRepositoryPort,
                getAuthenticatedUserPort
        );

        Role librarianRole = new Role(1L, "LIBRARIAN", "Librarian Role");
        sampleStaff = new User(1L, "staff", "hash", "Staff Name", "staff@test.com", "0912345678", true, null, LocalDateTime.now(), LocalDateTime.now(), java.util.Set.of(librarianRole));

        sampleReader = Reader.builder()
                .id(10L)
                .cardNumber("RD-001")
                .name("Reader Test")
                .email("reader@test.com")
                .phoneNumber("0987654321")
                .address("Ha Noi")
                .cardStatus(CardStatus.ACTIVE)
                .cardIssuedAt(LocalDate.now())
                .cardExpiryAt(LocalDate.now().plusYears(1))
                .isActive(true)
                .build();

        sampleBook = new Book(
                100L,
                "Java Programming",
                "Author A",
                "9780134494166",
                "Description",
                null,
                "Publisher X",
                (short) 2023,
                "Shelf A1",
                10,
                5,
                1L,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("createBorrowSlip: Tạo phiếu mượn thành công")
    void createBorrowSlip_Success() {
        CreateBorrowSlipCommand command = new CreateBorrowSlipCommand(10L, List.of(100L), 1L, 14, "Ghi chu");

        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(sampleStaff);
        when(readerRepositoryPort.findById(10L)).thenReturn(Optional.of(sampleReader));
        doNothing().when(checkBorrowEligibilityUseCase).validateBorrowEligibility(10L, 1);
        when(bookRepositoryPort.findByIdForUpdate(100L)).thenReturn(Optional.of(sampleBook));

        LocalDateTime now = LocalDateTime.now();
        BorrowSlip mockSavedSlip = new BorrowSlip(
                1000L,
                "BM12345",
                10L,
                1L,
                now,
                now.plusDays(14),
                null,
                org.example.librarymanagement.domain.enums.BorrowSlipStatus.BORROWING,
                "Ghi chu",
                now,
                now
        );
        when(saveBorrowSlipPort.save(any(BorrowSlip.class))).thenReturn(mockSavedSlip);

        BorrowSlipResponseDto response = createBorrowSlipService.createBorrowSlip(command);

        assertNotNull(response);
        assertEquals(10L, response.readerId());
        assertEquals("RD-001", response.readerCardNumber());
        assertEquals(1, response.totalBooks());

        verify(bookRepositoryPort).save(sampleBook);
        verify(saveBorrowSlipPort).save(any(BorrowSlip.class));
        verify(borrowDetailsRepositoryPort).saveAll(any());
    }

    @Test
    @DisplayName("createBorrowSlip: Ném ReaderNotFoundException khi không tìm thấy độc giả")
    void createBorrowSlip_ReaderNotFound() {
        CreateBorrowSlipCommand command = new CreateBorrowSlipCommand(99L, List.of(100L), 1L, 14, null);

        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(sampleStaff);
        when(readerRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ReaderNotFoundException.class, () -> createBorrowSlipService.createBorrowSlip(command));
    }

    @Test
    @DisplayName("createBorrowSlip: Ném DomainException khi sách hết số lượng khả dụng")
    void createBorrowSlip_BookOutOfStock() {
        Book outOfStockBook = new Book(
                101L, "OutOfStock Book", "Author B", "9780134494166", "Desc", null, "Pub", (short) 2022, "A2", 5, 0, 1L, true, LocalDateTime.now(), LocalDateTime.now()
        );

        CreateBorrowSlipCommand command = new CreateBorrowSlipCommand(10L, List.of(101L), 1L, 14, null);

        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(sampleStaff);
        when(readerRepositoryPort.findById(10L)).thenReturn(Optional.of(sampleReader));
        doNothing().when(checkBorrowEligibilityUseCase).validateBorrowEligibility(10L, 1);
        when(bookRepositoryPort.findByIdForUpdate(101L)).thenReturn(Optional.of(outOfStockBook));

        DomainException ex = assertThrows(DomainException.class, () -> createBorrowSlipService.createBorrowSlip(command));
        assertEquals("Book 'OutOfStock Book' is out of stock or inactive", ex.getMessage());
    }
}
