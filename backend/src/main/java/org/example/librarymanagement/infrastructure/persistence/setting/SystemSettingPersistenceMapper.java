package org.example.librarymanagement.infrastructure.persistence.setting;

import org.example.librarymanagement.domain.entity.SystemSetting;

public final class SystemSettingPersistenceMapper {

    private SystemSettingPersistenceMapper() {
    }

    public static SystemSetting toDomain(SystemSettingJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        return new SystemSetting(
                jpaEntity.getId(),
                jpaEntity.getSettingKey(),
                jpaEntity.getSettingValue(),
                jpaEntity.getDescription(),
                jpaEntity.getUpdatedByUserId(),
                jpaEntity.getCreatedAt(),
                jpaEntity.getUpdatedAt()
        );
    }

    public static SystemSettingJpaEntity toJpaEntity(SystemSetting domainEntity) {
        if (domainEntity == null) {
            return null;
        }
        return new SystemSettingJpaEntity(
                domainEntity.getId(),
                domainEntity.getSettingKey(),
                domainEntity.getSettingValue(),
                domainEntity.getDescription(),
                domainEntity.getUpdatedByUserId(),
                domainEntity.getCreatedAt(),
                domainEntity.getUpdatedAt()
        );
    }
}
