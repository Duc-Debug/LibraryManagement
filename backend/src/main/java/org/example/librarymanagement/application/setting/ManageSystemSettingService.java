package org.example.librarymanagement.application.setting;

import java.util.List;
import java.util.Objects;

import org.example.librarymanagement.application.borrow.CheckBorrowEligibilityService;
import org.example.librarymanagement.application.shared.ValidationException;
import org.example.librarymanagement.domain.entity.SystemSetting;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.setting.SystemSettingNotFoundException;
import org.example.librarymanagement.domain.exceptions.shared.UnauthenticatedException;
import org.example.librarymanagement.domain.policies.AccountLockPolicy;
import org.example.librarymanagement.domain.policies.AuthorizationAccessPolicy;
import org.example.librarymanagement.port.dtos.setting.SystemSettingResponseDto;
import org.example.librarymanagement.port.inbound.setting.ManageSystemSettingUseCase;
import org.example.librarymanagement.port.outbound.setting.SaveSystemSettingPort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;

public class ManageSystemSettingService implements ManageSystemSettingUseCase {

    private final SaveSystemSettingPort saveSystemSettingPort;
    private final GetAuthenticatedUserPort getAuthenticatedUserPort;

    public ManageSystemSettingService(
            SaveSystemSettingPort saveSystemSettingPort,
            GetAuthenticatedUserPort getAuthenticatedUserPort
    ) {
        this.saveSystemSettingPort = Objects.requireNonNull(saveSystemSettingPort, "SaveSystemSettingPort must not be null");
        this.getAuthenticatedUserPort = Objects.requireNonNull(getAuthenticatedUserPort, "GetAuthenticatedUserPort must not be null");
    }

    @Override
    public List<SystemSettingResponseDto> getAllSettings() {
        verifyStaffAccess();
        return saveSystemSettingPort.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public SystemSettingResponseDto getSettingByKey(String settingKey) {
        verifyStaffAccess();
        validateSettingKey(settingKey);

        SystemSetting setting = saveSystemSettingPort.findBySettingKey(settingKey.trim().toUpperCase())
                .orElseThrow(() -> new SystemSettingNotFoundException(settingKey));

        return toDto(setting);
    }

    @Override
    public SystemSettingResponseDto updateSetting(String settingKey, String newValue, String description) {
        // CHỈ ADMIN MỚI ĐƯỢC PHÉP CHỈNH SỬA CẤU HÌNH HỆ THỐNG
        User currentAdmin = verifyAdminAccess();
        validateSettingKey(settingKey);

        String normalizedKey = settingKey.trim().toUpperCase();
        validateSettingValue(normalizedKey, newValue);

        SystemSetting setting = saveSystemSettingPort.findBySettingKey(normalizedKey)
                .orElseGet(() -> SystemSetting.create(normalizedKey, newValue, description, currentAdmin.getId()));

        if (setting.getId() != null) {
            setting.updateValue(newValue, description, currentAdmin.getId());
        }

        SystemSetting saved = saveSystemSettingPort.save(setting);
        return toDto(saved);
    }

    private User verifyAdminAccess() {
        User currentUser = getAuthenticatedUserPort.getCurrentUser();
        if (currentUser == null) {
            throw new UnauthenticatedException("User is unauthenticated");
        }
        AccountLockPolicy.validateAccountActive(currentUser);
        AuthorizationAccessPolicy.validateAdminAccess(currentUser);
        return currentUser;
    }

    private void verifyStaffAccess() {
        User currentUser = getAuthenticatedUserPort.getCurrentUser();
        if (currentUser == null) {
            throw new UnauthenticatedException("User is unauthenticated");
        }
        AccountLockPolicy.validateAccountActive(currentUser);
        AuthorizationAccessPolicy.validateStaffAccess(currentUser);
    }

    private void validateSettingKey(String key) {
        if (key == null || key.isBlank()) {
            throw new ValidationException("Setting key must not be blank");
        }
    }

    private void validateSettingValue(String key, String value) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Setting value must not be blank");
        }

        // Kiểm tra hợp lệ cho giới hạn mượn sách đồng thời
        if (CheckBorrowEligibilityService.SETTING_KEY_MAX_BORROW_LIMIT.equalsIgnoreCase(key)) {
            try {
                int limit = Integer.parseInt(value.trim());
                if (limit <= 0) {
                    throw new ValidationException("Max concurrent borrow limit must be greater than 0");
                }
            } catch (NumberFormatException e) {
                throw new ValidationException("Max concurrent borrow limit must be a valid integer number");
            }
        }
    }

    private SystemSettingResponseDto toDto(SystemSetting setting) {
        return new SystemSettingResponseDto(
                setting.getId(),
                setting.getSettingKey(),
                setting.getSettingValue(),
                setting.getDescription(),
                setting.getUpdatedByUserId(),
                setting.getUpdatedAt()
        );
    }
}
