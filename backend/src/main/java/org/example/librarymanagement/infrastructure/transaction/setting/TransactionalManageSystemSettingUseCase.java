package org.example.librarymanagement.infrastructure.transaction.setting;

import java.util.List;
import java.util.Objects;

import org.example.librarymanagement.port.dtos.setting.SystemSettingResponseDto;
import org.example.librarymanagement.port.inbound.setting.ManageSystemSettingUseCase;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transactional Decorator Proxy for ManageSystemSettingUseCase
 * Bọc Spring @Transactional ngoài Pure Java ManageSystemSettingService
 */
public class TransactionalManageSystemSettingUseCase implements ManageSystemSettingUseCase {

    private final ManageSystemSettingUseCase delegate;

    public TransactionalManageSystemSettingUseCase(ManageSystemSettingUseCase delegate) {
        this.delegate = Objects.requireNonNull(delegate, "ManageSystemSettingUseCase delegate must not be null");
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemSettingResponseDto> getAllSettings() {
        return delegate.getAllSettings();
    }

    @Override
    @Transactional(readOnly = true)
    public SystemSettingResponseDto getSettingByKey(String settingKey) {
        return delegate.getSettingByKey(settingKey);
    }

    @Override
    @Transactional
    public SystemSettingResponseDto updateSetting(String settingKey, String newValue, String description) {
        return delegate.updateSetting(settingKey, newValue, description);
    }
}
