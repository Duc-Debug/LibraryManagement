package org.example.librarymanagement.infrastructure.exporter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.example.librarymanagement.port.dtos.report.BorrowReportItemDto;
import org.example.librarymanagement.port.dtos.report.BorrowReportSummaryDto;
import org.example.librarymanagement.port.dtos.report.ReportFormat;
import org.example.librarymanagement.port.outbound.report.ReportExporterPort;
import org.springframework.stereotype.Component;

@Component
public class CsvReportExporterAdapter implements ReportExporterPort {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void exportToStream(
            ReportFormat format,
            LocalDate startDate,
            LocalDate endDate,
            BorrowReportSummaryDto summary,
            List<BorrowReportItemDto> items,
            OutputStream outputStream
    ) {
        try {
            outputStream.write(0xEF);
            outputStream.write(0xBB);
            outputStream.write(0xBF);

            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            writer.println("BÁO CÁO MƯỢN - TRẢ SÁCH THƯ VIỆN");
            writer.println("Khoảng thời gian: " + (startDate != null ? startDate.format(DATE_ONLY_FORMATTER) : "Tất cả")
                    + " đến " + (endDate != null ? endDate.format(DATE_ONLY_FORMATTER) : "Hôm nay"));
            writer.println();

            if (summary != null) {
                writer.println("THỐNG KÊ TỔNG QUAN");
                writer.println("Tổng số phiếu mượn," + summary.totalSlips());
                writer.println("Tổng số lượt sách mượn," + summary.totalBooksBorrowed());
                writer.println("Đã trả," + summary.totalReturned());
                writer.println("Quá hạn," + summary.totalOverdue());
                writer.println("Đang mượn," + summary.totalBorrowing());
                writer.println("Tổng tiền phạt (VND)," + (summary.totalFineAmount() != null ? summary.totalFineAmount().toPlainString() : "0"));
                writer.println();
            }

            writer.println("STT,Mã Phiếu,Mã Thẻ Độc Giả,Tên Độc Giả,Danh Sách Sách Mượn,Số Lượng,Ngày Mượn,Hạn Trả,Ngày Trả Thực Tế,Trạng Thái,Tiền Phạt (VND),Người Lập Phiếu");

            int stt = 1;
            for (BorrowReportItemDto item : items) {
                String borrowedAtStr = item.borrowedAt() != null ? item.borrowedAt().format(DATE_FORMATTER) : "";
                String dueAtStr = item.dueAt() != null ? item.dueAt().format(DATE_FORMATTER) : "";
                String returnedAtStr = item.actualReturnedAt() != null ? item.actualReturnedAt().format(DATE_FORMATTER) : "-";
                String statusStr = item.status() != null ? item.status().name() : "";
                String fineStr = item.fineAmount() != null ? item.fineAmount().toPlainString() : "0";

                writer.println(String.join(",",
                        String.valueOf(stt++),
                        escapeCsv(item.borrowCode()),
                        escapeCsv(item.readerCardCode()),
                        escapeCsv(item.readerName()),
                        escapeCsv(item.bookTitles()),
                        String.valueOf(item.totalBooks()),
                        escapeCsv(borrowedAtStr),
                        escapeCsv(dueAtStr),
                        escapeCsv(returnedAtStr),
                        escapeCsv(statusStr),
                        escapeCsv(fineStr),
                        escapeCsv(item.createdByUserName())
                ));
            }

            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException("Failed to stream borrow report CSV", e);
        }
    }

    @Override
    public byte[] exportToCsv(
            LocalDate startDate,
            LocalDate endDate,
            BorrowReportSummaryDto summary,
            List<BorrowReportItemDto> items
    ) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        exportToStream(ReportFormat.CSV, startDate, endDate, summary, items, baos);
        return baos.toByteArray();
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String str = value.trim();
        if (str.contains(",") || str.contains("\"") || str.contains("\n") || str.contains("\r")) {
            str = str.replace("\"", "\"\"");
            return "\"" + str + "\"";
        }
        return str;
    }
}
