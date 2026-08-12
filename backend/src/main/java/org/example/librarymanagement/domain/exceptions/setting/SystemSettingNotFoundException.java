package org.example.librarymanagement.domain.exceptions.setting;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class SystemSettingNotFoundException extends DomainException {

    public SystemSettingNotFoundException(String settingKey) {
        super("System setting not found with key: " + settingKey);
    }
}
