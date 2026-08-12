package org.example.librarymanagement.port.outbound.setting;

import java.util.List;
import java.util.Optional;

import org.example.librarymanagement.domain.entity.SystemSetting;

public interface SaveSystemSettingPort {

    Optional<SystemSetting> findBySettingKey(String settingKey);

    List<SystemSetting> findAll();

    SystemSetting save(SystemSetting systemSetting);
}
