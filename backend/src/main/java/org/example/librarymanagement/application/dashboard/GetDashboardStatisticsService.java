package org.example.librarymanagement.application.dashboard;

import java.util.Objects;

import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.shared.UnauthenticatedException;
import org.example.librarymanagement.domain.policies.AccountLockPolicy;
import org.example.librarymanagement.domain.policies.AuthorizationAccessPolicy;
import org.example.librarymanagement.port.dtos.dashboard.DashboardStatisticsDto;
import org.example.librarymanagement.port.inbound.dashboard.GetDashboardStatisticsUseCase;
import org.example.librarymanagement.port.outbound.dashboard.LoadDashboardStatisticsPort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;

public class GetDashboardStatisticsService implements GetDashboardStatisticsUseCase {

    private final LoadDashboardStatisticsPort loadDashboardStatisticsPort;
    private final GetAuthenticatedUserPort getAuthenticatedUserPort;

    public GetDashboardStatisticsService(
            LoadDashboardStatisticsPort loadDashboardStatisticsPort,
            GetAuthenticatedUserPort getAuthenticatedUserPort
    ) {
        this.loadDashboardStatisticsPort = Objects.requireNonNull(
                loadDashboardStatisticsPort,
                "LoadDashboardStatisticsPort must not be null"
        );
        this.getAuthenticatedUserPort = Objects.requireNonNull(
                getAuthenticatedUserPort,
                "GetAuthenticatedUserPort must not be null"
        );
    }

    @Override
    public DashboardStatisticsDto getStatistics() {
        verifyStaffAccess();
        return loadDashboardStatisticsPort.loadStatistics();
    }

    private void verifyStaffAccess() {
        User currentUser = getAuthenticatedUserPort.getCurrentUser();
        if (currentUser == null) {
            throw new UnauthenticatedException("User is unauthenticated");
        }

        AccountLockPolicy.validateAccountActive(currentUser);
        AuthorizationAccessPolicy.validateStaffAccess(currentUser);
    }
}
