package org.example.librarymanagement.infrastructure.transaction.dashboard;

import java.util.Objects;

import org.example.librarymanagement.port.dtos.dashboard.DashboardStatisticsDto;
import org.example.librarymanagement.port.inbound.dashboard.GetDashboardStatisticsUseCase;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalGetDashboardStatisticsUseCase implements GetDashboardStatisticsUseCase {

    private final GetDashboardStatisticsUseCase delegate;

    public TransactionalGetDashboardStatisticsUseCase(GetDashboardStatisticsUseCase delegate) {
        this.delegate = Objects.requireNonNull(delegate, "GetDashboardStatisticsUseCase delegate must not be null");
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatisticsDto getStatistics() {
        return delegate.getStatistics();
    }
}
