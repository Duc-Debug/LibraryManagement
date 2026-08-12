package org.example.librarymanagement.infrastructure.persistence.setting;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemSettingJpaRepository extends JpaRepository<SystemSettingJpaEntity, Long> {

    Optional<SystemSettingJpaEntity> findBySettingKey(String settingKey);

    boolean existsBySettingKey(String settingKey);
}
