package org.example.librarymanagement.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class SystemSetting {

    private Long id;
    private String settingKey;
    private String settingValue;
    private String description;
    private Long updatedByUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 1. Static Factory Method dùng khi Tạo Mới Setting
    public static SystemSetting create(String key, String value, String description, Long userId) {
        validateKey(key);
        LocalDateTime now = LocalDateTime.now();
        return new SystemSetting(
                null,
                key,
                value,
                description,
                userId,
                now,
                now
        );
    }

    // 2. Full-Args Constructor dùng khi Reconstitute từ Database
    public SystemSetting(
            Long id,
            String settingKey,
            String settingValue,
            String description,
            Long updatedByUserId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        validateKey(settingKey);

        this.id = id;
        this.settingKey = settingKey.trim().toUpperCase();
        this.settingValue = settingValue != null ? settingValue.trim() : null;
        this.description = description != null ? description.trim() : null;
        this.updatedByUserId = updatedByUserId;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    // ==================== DOMAIN BUSINESS BEHAVIORS ====================

    /**
     * Cập nhật giá trị cấu hình hệ thống
     */
    public void updateValue(String newValue, String newDescription, Long updatedByUserId) {
        this.settingValue = newValue != null ? newValue.trim() : null;
        if (newDescription != null) {
            this.description = newDescription.trim();
        }
        this.updatedByUserId = updatedByUserId;
        touch();
    }

    // ==================== HELPER VALIDATIONS ====================

    private static void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new DomainException("Setting key must not be blank");
        }
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== GETTERS ONLY (NO PUBLIC SETTERS) ====================

    public Long getId() {
        return id;
    }

    public String getSettingKey() {
        return settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public String getDescription() {
        return description;
    }

    public Long getUpdatedByUserId() {
        return updatedByUserId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemSetting setting = (SystemSetting) o;
        return Objects.equals(id, setting.id) || Objects.equals(settingKey, setting.settingKey);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : Objects.hash(settingKey);
    }

    @Override
    public String toString() {
        return "SystemSetting{" +
                "id=" + id +
                ", settingKey='" + settingKey + '\'' +
                ", settingValue='" + settingValue + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}