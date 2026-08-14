package org.example.librarymanagement.infrastructure.web.dashboard;

import org.example.librarymanagement.port.dtos.dashboard.DashboardStatisticsDto;
import org.example.librarymanagement.port.inbound.dashboard.GetDashboardStatisticsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({"/api/librarians/dashboard", "/api/dashboard"})
@RequiredArgsConstructor
public class DashboardController {

    private final GetDashboardStatisticsUseCase getDashboardStatisticsUseCase;

    /**
     * API: Lấy thông tin thống kê tổng hợp cho Bảng điều khiển (Dashboard Statistics)
     *
     * @return DTO chứa toàn bộ số liệu thống kê (Tổng tựa sách, Đang mượn, Độc giả, Phiếu quá hạn...)
     */
    @GetMapping("/statistics")
    public ResponseEntity<DashboardStatisticsDto> getDashboardStatistics() {
        DashboardStatisticsDto statistics = getDashboardStatisticsUseCase.getStatistics();
        return ResponseEntity.ok(statistics);
    }
}
