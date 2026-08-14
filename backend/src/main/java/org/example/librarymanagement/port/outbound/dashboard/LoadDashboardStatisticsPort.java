package org.example.librarymanagement.port.outbound.dashboard;

import org.example.librarymanagement.port.dtos.dashboard.DashboardStatisticsDto;

public interface LoadDashboardStatisticsPort {
    DashboardStatisticsDto loadStatistics();
}
