package org.example.librarymanagement.application.borrow;

import java.util.Objects;

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
    private final LoadBorrowSlipPort loadBorrowSlipPort;
    private final GetAuthenticatedUserPort getAuthenticatedUserPort;
    
    public GetBorrowSlipsService(LoadBorrowSlipPort loadBorrowSlipPort, GetAuthenticatedUserPort getAuthenticatedUserPort) {
        this.loadBorrowSlipPort = Objects.requireNonNull(loadBorrowSlipPort, "LoadBorrowSlipPort must not be null");
        this.getAuthenticatedUserPort = Objects.requireNonNull(getAuthenticatedUserPort, "GetAuthenticatedUserPort must not be null");
    }

    @Override
    public PageResult<BorrowSlipResponseDto> getBorrowSlips(BorrowSlipFilterQuery query)
    {
        // check xac thuc va phan quyen
        verifyStaffAccess();

        // chuan hoa dau vao
        BorrowSlipFilterQuery safeQuery = query != null ? query : new BorrowSlipFilterQuery(0, 10, null, null);
       int pageNumber = Math.max(0, safeQuery.page());
        int pageSize = safeQuery.size() <0 ? 10 : safeQuery.size();

        String keyword = safeQuery.keyword() != null ? safeQuery.keyword().trim() : null;


        // goi outbound portt de truy van du lieu persistence

        return loadBorrowSlipPort.findBorrowSlips(
            pageNumber,pageSize,safeQuery.status(),keyword
        );
    }

    private void verifyStaffAccess()
    {
        User currentUser = getAuthenticatedUserPort.getCurrentUser();
        if(currentUser == null )
        {
            throw new UnauthenticatedException("User is unauthenticated");
        }

        AccountLockPolicy.validateAccountActive(currentUser);
        AuthorizationAccessPolicy.validateStaffAccess(currentUser);
    }
}
