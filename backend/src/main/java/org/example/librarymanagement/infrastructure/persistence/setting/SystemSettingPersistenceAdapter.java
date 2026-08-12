package org.example.librarymanagement.infrastructure.persistence.setting;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.example.librarymanagement.domain.entity.SystemSetting;
import org.example.librarymanagement.port.outbound.setting.LoadSystemSettingPort;
import org.example.librarymanagement.port.outbound.setting.SaveSystemSettingPort;
import org.springframework.stereotype.Component;

@Component
public class SystemSettingPersistenceAdapter implements LoadSystemSettingPort, SaveSystemSettingPort {

    private final SystemSettingJpaRepository repository;

    public SystemSettingPersistenceAdapter(SystemSettingJpaRepository repository) {
        this.repository = Objects.requireNonNull(repository, "SystemSettingJpaRepository must not be null");
    }

    @Override
    public Optional<String> getSettingValueByKey(String settingKey) {
        if (settingKey == null || settingKey.isBlank()) {
            return Optional.empty();
        }
        return repository.findBySettingKey(settingKey.trim().toUpperCase())
                .map(SystemSettingJpaEntity::getSettingValue);
    }

    @Override
    public int getIntSetting(String settingKey, int defaultValue) {
        return getSettingValueByKey(settingKey)
                .map(val -> {
                    try {
                        return Integer.parseInt(val.trim());
                    } catch (NumberFormatException e) {
                        return defaultValue;
                    }
                })
                .orElse(defaultValue);
    }

    @Override
    public Optional<SystemSetting> findBySettingKey(String settingKey) {
        if (settingKey == null || settingKey.isBlank()) {
            return Optional.empty();
        }
        return repository.findBySettingKey(settingKey.trim().toUpperCase())
                .map(SystemSettingPersistenceMapper::toDomain);
    }

    @Override
    public List<SystemSetting> findAll() {
        return repository.findAll().stream()
                .map(SystemSettingPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public SystemSetting save(SystemSetting systemSetting) {
        if (systemSetting == null) {
            throw new IllegalArgumentException("SystemSetting domain entity must not be null");
        }
        SystemSettingJpaEntity jpaEntity = SystemSettingPersistenceMapper.toJpaEntity(systemSetting);
        SystemSettingJpaEntity savedEntity = repository.save(jpaEntity);
        return SystemSettingPersistenceMapper.toDomain(savedEntity);
    }
}
