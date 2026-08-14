package org.example.librarymanagement.infrastructure.persistence.borrow;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.example.librarymanagement.LibraryManagementApplication;
import org.example.librarymanagement.application.borrow.UpdateOverdueBorrowSlipsService;
import org.example.librarymanagement.domain.entity.BorrowSlip;
import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.port.outbound.borrow.LoadBorrowSlipPort;
import org.example.librarymanagement.port.outbound.borrow.SaveBorrowSlipPort;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration test cho {@link LoadBorrowSlipPort#findAllActivePastDue} và
 * {@link UpdateOverdueBorrowSlipsService} chạy trên H2 in-memory.
 *
 * <p>Tất cả test đều {@code @Transactional} → rollback sau mỗi test,
 * không ảnh hưởng lẫn nhau.</p>
 *
 * <p><b>Boundary matrix được kiểm tra:</b>
 * <pre>
 *   Status    | dueDate   | findAllActivePastDue(today) | sau execute()
 *   ----------+-----------+-----------------------------+--------------
 *   BORROWING | yesterday | found                       | → OVERDUE
 *   BORROWING | today     | NOT found                   | không đổi
 *   BORROWING | tomorrow  | NOT found                   | không đổi
 *   RETURNED  | yesterday | NOT found                   | không đổi
 *   OVERDUE   | yesterday | NOT found (idempotent)      | không đổi
 * </pre>
 * </p>
 */
@SpringBootTest(classes = LibraryManagementApplication.class)
@ActiveProfiles("test")
@Transactional
class BorrowSlipOverdueIntegrationTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate YESTERDAY = TODAY.minusDays(1);
    private static final LocalDate TOMORROW = TODAY.plusDays(1);

    @Autowired
    private BorrowSlipJpaRepository borrowSlipJpaRepository;

    @Autowired
    private LoadBorrowSlipPort loadBorrowSlipPort;

    @Autowired
    private SaveBorrowSlipPort saveBorrowSlipPort;

    // ==================== HELPER ====================

    private BorrowSlipJpaEntity saveJpa(String code, LocalDate dueDate, BorrowSlipStatus status) {
        LocalDateTime borrowedAt = LocalDateTime.of(2026, 7, 1, 10, 0);
        LocalDateTime dueAt = dueDate.atTime(23, 59, 59);
        LocalDateTime returnedAt = (status == BorrowSlipStatus.RETURNED)
                ? LocalDateTime.of(2026, 8, 10, 10, 0)
                : null;

        BorrowSlipJpaEntity entity = new BorrowSlipJpaEntity(
                null, code, 10L, 1L,
                borrowedAt, dueAt, returnedAt,
                status, null, borrowedAt, borrowedAt);
        return borrowSlipJpaRepository.save(entity);
    }

    // ==================== BOUNDARY TESTS: findAllActivePastDue ====================

    @Test
    @DisplayName("Repository: BORROWING + yesterday → được tìm thấy bởi findAllActivePastDue")
    void findAllActivePastDue_ReturnsBorrowingYesterdaySlip() {
        // Arrange
        saveJpa("BRW-YEST-01", YESTERDAY, BorrowSlipStatus.BORROWING);

        // Act
        List<BorrowSlip> result = loadBorrowSlipPort.findAllActivePastDue(TODAY);

        // Assert
        assertEquals(1, result.size());
        assertEquals(BorrowSlipStatus.BORROWING, result.get(0).getStatus());
    }

    @Test
    @DisplayName("Repository: BORROWING + today → KHÔNG được tìm thấy (dueAt = today, không phải trước)")
    void findAllActivePastDue_DoesNotReturnBorrowingTodaySlip() {
        // Arrange
        saveJpa("BRW-TODAY-01", TODAY, BorrowSlipStatus.BORROWING);

        // Act
        List<BorrowSlip> result = loadBorrowSlipPort.findAllActivePastDue(TODAY);

        // Assert
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Repository: BORROWING + tomorrow → KHÔNG được tìm thấy")
    void findAllActivePastDue_DoesNotReturnBorrowingTomorrowSlip() {
        // Arrange
        saveJpa("BRW-TMRW-01", TOMORROW, BorrowSlipStatus.BORROWING);

        // Act
        List<BorrowSlip> result = loadBorrowSlipPort.findAllActivePastDue(TODAY);

        // Assert
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Repository: RETURNED + yesterday → KHÔNG được tìm thấy (đã trả rồi)")
    void findAllActivePastDue_DoesNotReturnReturnedYesterdaySlip() {
        // Arrange
        saveJpa("BRW-RET-01", YESTERDAY, BorrowSlipStatus.RETURNED);

        // Act
        List<BorrowSlip> result = loadBorrowSlipPort.findAllActivePastDue(TODAY);

        // Assert
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Repository: OVERDUE + yesterday → KHÔNG được tìm thấy (idempotent: đã mark rồi)")
    void findAllActivePastDue_DoesNotReturnAlreadyOverdueSlip() {
        // Arrange — slip đã được đánh dấu OVERDUE trước đó
        saveJpa("BRW-OVD-01", YESTERDAY, BorrowSlipStatus.OVERDUE);

        // Act
        List<BorrowSlip> result = loadBorrowSlipPort.findAllActivePastDue(TODAY);

        // Assert
        assertEquals(0, result.size());
    }

    // ==================== END-TO-END: Service + Repository ====================

    @Test
    @DisplayName("E2E: executeOn(today) cập nhật BORROWING+yesterday → OVERDUE trong DB")
    void executeOn_UpdatesBorrowingYesterdayToOverdue() {
        // Arrange
        BorrowSlipJpaEntity entity = saveJpa("BRW-E2E-01", YESTERDAY, BorrowSlipStatus.BORROWING);
        UpdateOverdueBorrowSlipsService svc =
                new UpdateOverdueBorrowSlipsService(loadBorrowSlipPort, saveBorrowSlipPort);

        // Act
        int updated = svc.executeOn(TODAY);

        // Assert — service reports 1 updated
        assertEquals(1, updated);

        // Assert — DB reflects OVERDUE status
        BorrowSlipJpaEntity reloaded = borrowSlipJpaRepository.findById(entity.getId()).orElseThrow();
        assertEquals(BorrowSlipStatus.OVERDUE, reloaded.getStatus());
    }

    @Test
    @DisplayName("E2E: executeOn(today) KHÔNG cập nhật BORROWING+today")
    void executeOn_DoesNotUpdateBorrowingTodaySlip() {
        // Arrange
        BorrowSlipJpaEntity entity = saveJpa("BRW-E2E-02", TODAY, BorrowSlipStatus.BORROWING);
        UpdateOverdueBorrowSlipsService svc =
                new UpdateOverdueBorrowSlipsService(loadBorrowSlipPort, saveBorrowSlipPort);

        // Act
        int updated = svc.executeOn(TODAY);

        // Assert
        assertEquals(0, updated);

        BorrowSlipJpaEntity reloaded = borrowSlipJpaRepository.findById(entity.getId()).orElseThrow();
        assertEquals(BorrowSlipStatus.BORROWING, reloaded.getStatus());
    }

    @Test
    @DisplayName("E2E: executeOn(today) KHÔNG cập nhật BORROWING+tomorrow")
    void executeOn_DoesNotUpdateBorrowingTomorrowSlip() {
        // Arrange
        BorrowSlipJpaEntity entity = saveJpa("BRW-E2E-03", TOMORROW, BorrowSlipStatus.BORROWING);
        UpdateOverdueBorrowSlipsService svc =
                new UpdateOverdueBorrowSlipsService(loadBorrowSlipPort, saveBorrowSlipPort);

        // Act
        int updated = svc.executeOn(TODAY);

        // Assert
        assertEquals(0, updated);

        BorrowSlipJpaEntity reloaded = borrowSlipJpaRepository.findById(entity.getId()).orElseThrow();
        assertEquals(BorrowSlipStatus.BORROWING, reloaded.getStatus());
    }

    @Test
    @DisplayName("E2E: Mixed — chỉ phiếu yesterday bị cập nhật, today và tomorrow giữ nguyên")
    void executeOn_OnlyUpdatesYesterdaySlips_InMixedBatch() {
        // Arrange
        BorrowSlipJpaEntity yesterdaySlip = saveJpa("BRW-MIX-01", YESTERDAY, BorrowSlipStatus.BORROWING);
        BorrowSlipJpaEntity todaySlip    = saveJpa("BRW-MIX-02", TODAY,     BorrowSlipStatus.BORROWING);
        BorrowSlipJpaEntity tomorrowSlip = saveJpa("BRW-MIX-03", TOMORROW,  BorrowSlipStatus.BORROWING);
        UpdateOverdueBorrowSlipsService svc =
                new UpdateOverdueBorrowSlipsService(loadBorrowSlipPort, saveBorrowSlipPort);

        // Act
        int updated = svc.executeOn(TODAY);

        // Assert
        assertEquals(1, updated);
        assertEquals(BorrowSlipStatus.OVERDUE,
                borrowSlipJpaRepository.findById(yesterdaySlip.getId()).orElseThrow().getStatus());
        assertEquals(BorrowSlipStatus.BORROWING,
                borrowSlipJpaRepository.findById(todaySlip.getId()).orElseThrow().getStatus());
        assertEquals(BorrowSlipStatus.BORROWING,
                borrowSlipJpaRepository.findById(tomorrowSlip.getId()).orElseThrow().getStatus());
    }
}
