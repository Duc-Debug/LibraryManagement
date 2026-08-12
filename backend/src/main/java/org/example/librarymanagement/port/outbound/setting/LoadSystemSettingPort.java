package org.example.librarymanagement.port.outbound.setting;

import java.util.Optional;

public interface LoadSystemSettingPort {

    /**
     * Lấy giá trị cấu hình theo key
     *
     * @param settingKey Tên key cấu hình
     * @return Optional chuỗi giá trị cấu hình
     */
    Optional<String> getSettingValueByKey(String settingKey);

    /**
     * Lấy giá trị cấu hình dạng số nguyên, fallback về giá trị mặc định nếu không tìm thấy hoặc giá trị không hợp lệ
     *
     * @param settingKey Tên key cấu hình
     * @param defaultValue Giá trị mặc định
     * @return Số nguyên giá trị cấu hình
     */
    int getIntSetting(String settingKey, int defaultValue);
}
