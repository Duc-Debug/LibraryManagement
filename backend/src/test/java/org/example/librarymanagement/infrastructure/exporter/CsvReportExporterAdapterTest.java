package org.example.librarymanagement.infrastructure.exporter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.port.dtos.report.BorrowReportItemDto;
import org.example.librarymanagement.port.dtos.report.BorrowReportSummaryDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class CsvReportExporterAdapterTest {

    @Test
    void exportToCsv_ShouldIncludeBomAndHeader() {
        CsvReportExporterAdapter adapter = new CsvReportExporterAdapter();

        BorrowReportSummaryDto summary = new BorrowReportSummaryDto(1, 2, 1, 0, 0, BigDecimal.ZERO);
        BorrowReportItemDto item = new BorrowReportItemDto(
                1L, "PM001", "CARD123", "Nguyễn Văn Ánh", "Lập trình Java Căn Bản", 2,
                LocalDateTime.of(2026, 1, 1, 10, 0),
                LocalDateTime.of(2026, 1, 15, 10, 0),
                LocalDateTime.of(2026, 1, 10, 10, 0),
                BorrowSlipStatus.RETURNED,
                BigDecimal.ZERO,
                "Admin User"
        );

        byte[] csvBytes = adapter.exportToCsv(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 15), summary, List.of(item));

        assertNotNull(csvBytes);
        assertTrue(csvBytes.length > 3);

        // Kiểm tra tiền tố UTF-8 BOM: 0xEF, 0xBB, 0xBF
        assertEquals((byte) 0xEF, csvBytes[0]);
        assertEquals((byte) 0xBB, csvBytes[1]);
        assertEquals((byte) 0xBF, csvBytes[2]);

        String csvContent = new String(csvBytes, java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(csvContent.contains("BÁO CÁO MƯỢN - TRẢ SÁCH THƯ VIỆN"));
        assertTrue(csvContent.contains("Nguyễn Văn Ánh"));
        assertTrue(csvContent.contains("Lập trình Java Căn Bản"));
    }
}
