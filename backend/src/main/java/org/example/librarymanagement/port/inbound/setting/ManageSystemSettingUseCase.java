package org.example.librarymanagement.port.inbound.setting;

import java.util.List;

import org.example.librarymanagement.port.dtos.setting.SystemSettingResponseDto;

public interface ManageSystemSettingUseCase {

    /**
     * Lấy toàn bộ danh sách cấu hình hệ thống (Dành cho Thủ thư / Admin)
     */
    List<SystemSettingResponseDto> getAllSettings();

    /**
     * Lấy chi tiết cấu hình theo key
     */
    SystemSettingResponseDto getSettingByKey(String settingKey);

    /**
     * Cập nhật giá trị cấu hình hệ thống (CHỈ ADMIN CÓ QUYỀN THỰC HIỆN)
     *
     * @param settingKey Tên key cấu hình
     * @param newValue Giá trị mới
     * @param description Mô tả cập nhật (Tùy chọn)
     * @return DTO thông tin cấu hình sau khi cập nhật
     */
    SystemSettingResponseDto updateSetting(String settingKey, String newValue, String description);
}
