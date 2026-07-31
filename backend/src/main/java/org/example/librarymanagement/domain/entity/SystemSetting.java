package org.example.librarymanagement.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.librarymanagement.domain.exceptions.DomainException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemSetting {

    private UUID id;
    private String settingKey;
    private String settingValue;
    private String description;
    private UUID updatedByUserId; 
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SystemSetting(String settingKey, String settingValue, String description, UUID updatedByUserId) {
        this.settingKey = requireNotBlank(settingKey, "Setting key cannot be empty.");
        this.settingValue = requireNotBlank(settingValue, "Setting value cannot be empty.");
        this.description = requireNotBlank(description, "Description cannot be empty.");
        this.updatedByUserId = updatedByUserId;
        
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // Phương thức cập nhật giá trị cài đặt
    public void updateValue(String newValue, String newDescription, UUID userId) {
        this.settingValue = requireNotBlank(newValue, "Setting value cannot be empty.");
        if (newDescription != null && !newDescription.isBlank()) {
            this.description = newDescription.trim();
        }
        this.updatedByUserId = userId;
        this.updatedAt = LocalDateTime.now();
    }

    private static String requireNotBlank(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new DomainException(errorMessage);
        }
        return value.trim();
    }
}