package org.example.librarymanagement.application.borrow;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

import org.example.librarymanagement.domain.entity.BorrowSlip;
import org.example.librarymanagement.port.inbound.borrow.UpdateOverdueBorrowSlipsUseCase;
import org.example.librarymanagement.port.outbound.borrow.LoadBorrowSlipPort;
import org.example.librarymanagement.port.outbound.borrow.SaveBorrowSlipPort;

/**
 * Application service: Tìm tất cả phiếu mượn BORROWING đã quá hạn tính đến
 * {@code referenceDate} (mặc định hôm nay), gọi {@code markOverdue()} trên mỗi
 * phiếu, rồi lưu hàng loạt bằng {@link SaveBorrowSlipPort#saveAll}.
 *
 * <p>Logic được thiết kế idempotent: nếu scheduler chạy lại, các phiếu đã ở
 * trạng thái OVERDUE sẽ không được load bởi query (query chỉ lấy BORROWING).</p>
 */
public class UpdateOverdueBorrowSlipsService implements UpdateOverdueBorrowSlipsUseCase {

    private final LoadBorrowSlipPort loadBorrowSlipPort;
    private final SaveBorrowSlipPort saveBorrowSlipPort;

    public UpdateOverdueBorrowSlipsService(
            LoadBorrowSlipPort loadBorrowSlipPort,
            SaveBorrowSlipPort saveBorrowSlipPort) {
        this.loadBorrowSlipPort = Objects.requireNonNull(
                loadBorrowSlipPort, "LoadBorrowSlipPort must not be null");
        this.saveBorrowSlipPort = Objects.requireNonNull(
                saveBorrowSlipPort, "SaveBorrowSlipPort must not be null");
    }

    @Override
    public int execute() {
        return executeOn(LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")));
    }

    /**
     * Overload cho phép truyền ngày tham chiếu tùy ý — hữu ích trong unit test
     * để không phụ thuộc vào {@code LocalDate.now()}.
     */
    public int executeOn(LocalDate referenceDate) {
        Objects.requireNonNull(referenceDate, "referenceDate must not be null");

        List<BorrowSlip> overdueSlips = loadBorrowSlipPort.findAllActivePastDue(referenceDate);

        if (overdueSlips.isEmpty()) {
            return 0;
        }

        overdueSlips.forEach(BorrowSlip::markOverdue);
        saveBorrowSlipPort.saveAll(overdueSlips);

        return overdueSlips.size();
    }
}
