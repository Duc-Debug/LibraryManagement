package org.example.librarymanagement.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class SystemSetting {

    private UUID id;
    private String settingKey;
    private String settingValue;
    private String description;
    private UUID userId;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public SystemSetting() {
    }

    public SystemSetting(UUID id,
            String key,
            String value,
            String description,
            UUID userId,
            LocalDateTime createAt,
            LocalDateTime updateAt) {

        setId(id);
        setSettingKey(key);
        setSettingValue(value);
        setDescription(description);
        setUserId(userId);
        setCreateAt(createAt);
        setUpdateAt(updateAt);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        if (id != null) {
            throw new DomainException("Id must be greater than 0.");
        }
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new DomainException("Description cannot be empty.");
        }
        this.description = description;
    }

    @Override
    public String toString() {
        return "SystemSetting{" +
                "id=" + id +
                ", key=" + settingKey +
                ", value='" + settingValue + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        if (settingValue == null || settingValue.trim().isEmpty()) {
            throw new DomainException("Setting value cannot be empty.");
        }
        this.settingValue = settingValue;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }
}