package org.example.librarymanagement.infrastructure.persistence.setting;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.librarymanagement.domain.entity.SystemSetting;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SystemSettingPersistenceAdapterTest {

    @Mock
    private SystemSettingJpaRepository repository;

    private SystemSettingPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new SystemSettingPersistenceAdapter(repository);
    }

    @Test
    @DisplayName("getSettingValueByKey - Return value string when found")
    void getSettingValueByKey_Found() {
        SystemSettingJpaEntity entity = new SystemSettingJpaEntity(
                1L, "MAX_CONCURRENT_BORROW_BOOKS", "5", "Desc", 1L, LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.findBySettingKey("MAX_CONCURRENT_BORROW_BOOKS")).thenReturn(Optional.of(entity));

        Optional<String> val = adapter.getSettingValueByKey("MAX_CONCURRENT_BORROW_BOOKS");

        assertTrue(val.isPresent());
        assertEquals("5", val.get());
    }

    @Test
    @DisplayName("getIntSetting - Return parsed integer value, or defaultValue on format error")
    void getIntSetting_Cases() {
        SystemSettingJpaEntity entityValid = new SystemSettingJpaEntity(
                1L, "MAX_CONCURRENT_BORROW_BOOKS", "5", "Desc", 1L, LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.findBySettingKey("MAX_CONCURRENT_BORROW_BOOKS")).thenReturn(Optional.of(entityValid));

        assertEquals(5, adapter.getIntSetting("MAX_CONCURRENT_BORROW_BOOKS", 10));

        when(repository.findBySettingKey("NON_EXISTING")).thenReturn(Optional.empty());
        assertEquals(10, adapter.getIntSetting("NON_EXISTING", 10));
    }

    @Test
    @DisplayName("findBySettingKey - Return domain entity when found")
    void findBySettingKey_Found() {
        SystemSettingJpaEntity entity = new SystemSettingJpaEntity(
                1L, "MAX_CONCURRENT_BORROW_BOOKS", "5", "Desc", 1L, LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.findBySettingKey("MAX_CONCURRENT_BORROW_BOOKS")).thenReturn(Optional.of(entity));

        Optional<SystemSetting> domain = adapter.findBySettingKey("MAX_CONCURRENT_BORROW_BOOKS");

        assertTrue(domain.isPresent());
        assertEquals("MAX_CONCURRENT_BORROW_BOOKS", domain.get().getSettingKey());
        assertEquals("5", domain.get().getSettingValue());
    }

    @Test
    @DisplayName("findAll - Return list of domain entities")
    void findAll_Success() {
        SystemSettingJpaEntity entity = new SystemSettingJpaEntity(
                1L, "MAX_CONCURRENT_BORROW_BOOKS", "5", "Desc", 1L, LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.findAll()).thenReturn(List.of(entity));

        List<SystemSetting> list = adapter.findAll();

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    @DisplayName("save - Map domain to JPA entity, save, and map back")
    void save_Success() {
        SystemSetting domain = SystemSetting.create("MAX_CONCURRENT_BORROW_BOOKS", "7", "Updated", 1L);
        SystemSettingJpaEntity savedJpa = new SystemSettingJpaEntity(
                1L, "MAX_CONCURRENT_BORROW_BOOKS", "7", "Updated", 1L, LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.save(any(SystemSettingJpaEntity.class))).thenReturn(savedJpa);

        SystemSetting result = adapter.save(domain);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("MAX_CONCURRENT_BORROW_BOOKS", result.getSettingKey());
        assertEquals("7", result.getSettingValue());
        verify(repository).save(any(SystemSettingJpaEntity.class));
    }
}
