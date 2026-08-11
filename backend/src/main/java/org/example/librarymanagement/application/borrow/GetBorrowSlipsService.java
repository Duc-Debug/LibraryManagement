package org.example.librarymanagement.application.borrow;

import java.util.Objects;

import org.example.librarymanagement.application.shared.ValidationException;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.shared.UnauthenticatedException;
import org.example.librarymanagement.domain.policies.AccountLockPolicy;
import org.example.librarymanagement.domain.policies.AuthorizationAccessPolicy;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipFilterQuery;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipResponseDto;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.inbound.borrow.BorrowSlipsUseCase;
import org.example.librarymanagement.port.outbound.borrow.LoadBorrowSlipPort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;

public class GetBorrowSlipsService implements BorrowSlipsUseCase {

    public static final int MAX_PAGE_SIZE = 100;

    private final LoadBorrowSlipPort loadBorrowSlipPort;
    private final GetAuthenticatedUserPort getAuthenticatedUserPort;

    public GetBorrowSlipsService(
            LoadBorrowSlipPort loadBorrowSlipPort,
            GetAuthenticatedUserPort getAuthenticatedUserPort
    ) {
        this.loadBorrowSlipPort = Objects.requireNonNull(
                loadBorrowSlipPort,
                "LoadBorrowSlipPort must not be null"
        );
        this.getAuthenticatedUserPort = Objects.requireNonNull(
                getAuthenticatedUserPort,
                "GetAuthenticatedUserPort must not be null"
        );
    }

    @Override
    public PageResult<BorrowSlipResponseDto> getBorrowSlips(BorrowSlipFilterQuery query) {
        // 1. Kiểm tra xác thực và phân quyền
        verifyStaffAccess();

        // 2. Validate & Enforce ranh giới tham số tại Use-Case Boundary
        if (query == null) {
            throw new ValidationException("Filter query must not be null");
        }
        if (query.page() < 0) {
            throw new ValidationException("Page index must be greater than or equal to 0");
        }
        if (query.size() <= 0) {
            throw new ValidationException("Page size must be greater than 0");
        }
        if (query.size() > MAX_PAGE_SIZE) {
            throw new ValidationException("Page size must not exceed " + MAX_PAGE_SIZE);
        }

        String keyword = (query.keyword() != null && !query.keyword().isBlank())
                ? query.keyword().trim()
                : null;

        // 3. Gọi outbound port để truy vấn dữ liệu
        return loadBorrowSlipPort.findBorrowSlips(
                query.page(),
                query.size(),
                query.status(),
                keyword
        );
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
