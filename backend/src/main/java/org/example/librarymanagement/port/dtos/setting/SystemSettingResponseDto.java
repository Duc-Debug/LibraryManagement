package org.example.librarymanagement.port.dtos.setting;

import java.time.LocalDateTime;

public record SystemSettingResponseDto(
        Long id,
        String settingKey,
        String settingValue,
        String description,
        Long updatedByUserId,
        LocalDateTime updatedAt
) {
}
