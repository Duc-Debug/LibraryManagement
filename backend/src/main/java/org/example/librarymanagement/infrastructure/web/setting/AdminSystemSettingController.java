package org.example.librarymanagement.infrastructure.web.setting;

import java.util.List;

import org.example.librarymanagement.port.dtos.setting.SystemSettingResponseDto;
import org.example.librarymanagement.port.inbound.setting.ManageSystemSettingUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
@Validated
public class AdminSystemSettingController {

    private final ManageSystemSettingUseCase manageSystemSettingUseCase;

    /**
     * API: Lấy toàn bộ danh sách cấu hình hệ thống
     */
    @GetMapping
    public ResponseEntity<List<SystemSettingResponseDto>> getAllSettings() {
        List<SystemSettingResponseDto> result = manageSystemSettingUseCase.getAllSettings();
        return ResponseEntity.ok(result);
    }

    /**
     * API: Lấy chi tiết cấu hình theo Setting Key
     */
    @GetMapping("/{settingKey}")
    public ResponseEntity<SystemSettingResponseDto> getSettingByKey(
            @PathVariable @NotBlank(message = "Setting key must not be blank") String settingKey
    ) {
        SystemSettingResponseDto result = manageSystemSettingUseCase.getSettingByKey(settingKey);
        return ResponseEntity.ok(result);
    }

    /**
     * API: Cập nhật giá trị cấu hình hệ thống (CHỈ ADMIN CÓ QUYỀN THỰC HIỆN)
     */
    @PutMapping("/{settingKey}")
    public ResponseEntity<SystemSettingResponseDto> updateSetting(
            @PathVariable @NotBlank(message = "Setting key must not be blank") String settingKey,
            @Valid @RequestBody UpdateSettingRequest request
    ) {
        SystemSettingResponseDto result = manageSystemSettingUseCase.updateSetting(
                settingKey,
                request.settingValue(),
                request.description()
        );
        return ResponseEntity.ok(result);
    }
}
