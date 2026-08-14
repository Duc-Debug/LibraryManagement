package org.example.librarymanagement.port.inbound.dashboard;

import org.example.librarymanagement.port.dtos.dashboard.DashboardStatisticsDto;

public interface GetDashboardStatisticsUseCase {
    DashboardStatisticsDto getStatistics();
}
