package org.example.librarymanagement.domain.entity;

public class SystemSetting {

    private Long id;
    private SystemSettingKey key;
    private String value;
    private String description;

    public SystemSetting() {
    }

    public SystemSetting(Long id,
                         SystemSettingKey key,
                         String value,
                         String description) {

        setId(id);
        setKey(key);
        setValue(value);
        setDescription(description);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("Id must be greater than 0.");
        }
        this.id = id;
    }

    public SystemSettingKey getKey() {
        return key;
    }

    public void setKey(SystemSettingKey key) {
        if (key == null) {
            throw new IllegalArgumentException("Setting key cannot be null.");
        }
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Setting value cannot be empty.");
        }
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty.");
        }
        this.description = description;
    }

    @Override
    public String toString() {
        return "SystemSetting{" +
                "id=" + id +
                ", key=" + key +
                ", value='" + value + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}