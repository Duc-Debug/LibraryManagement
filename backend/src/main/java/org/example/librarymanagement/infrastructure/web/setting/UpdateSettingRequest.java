package org.example.librarymanagement.infrastructure.web.setting;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSettingRequest(
        @NotBlank(message = "Setting value must not be blank")
        @Size(max = 255, message = "Setting value must not exceed 255 characters")
        String settingValue,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description
) {
}
