package org.example.librarymanagement.infrastructure.config;

import org.example.librarymanagement.application.dashboard.GetDashboardStatisticsService;
import org.example.librarymanagement.infrastructure.transaction.dashboard.TransactionalGetDashboardStatisticsUseCase;
import org.example.librarymanagement.port.inbound.dashboard.GetDashboardStatisticsUseCase;
import org.example.librarymanagement.port.outbound.dashboard.LoadDashboardStatisticsPort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DashboardUseCaseConfig {

    @Bean
    public GetDashboardStatisticsUseCase getDashboardStatisticsUseCase(
            LoadDashboardStatisticsPort loadDashboardStatisticsPort,
            GetAuthenticatedUserPort getAuthenticatedUserPort
    ) {
        GetDashboardStatisticsService service = new GetDashboardStatisticsService(
                loadDashboardStatisticsPort,
                getAuthenticatedUserPort
        );
        return new TransactionalGetDashboardStatisticsUseCase(service);
    }
}
