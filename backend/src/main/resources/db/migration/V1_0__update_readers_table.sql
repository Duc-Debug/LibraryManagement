CREATE TABLE IF NOT EXISTS readers (
  id BIGINT NOT NULL AUTO_INCREMENT,
  card_code VARCHAR(30) NOT NULL,
  full_name VARCHAR(150) NOT NULL,
  email VARCHAR(100) NULL,
  phone VARCHAR(20) NULL,
  address VARCHAR(255) NULL,
  card_status ENUM('ACTIVE','EXPIRED','LOCKED') NULL DEFAULT 'ACTIVE',
  card_issued_at DATE NULL,
  card_expiry_date DATE NULL,
  active TINYINT(1) NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created_by_user_id BIGINT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_reader_card (card_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET @sql = (
  SELECT IF(
    EXISTS (
      SELECT 1
      FROM information_schema.columns
      WHERE table_schema = DATABASE()
        AND table_name = 'readers'
        AND column_name = 'card_number'
    )
    AND NOT EXISTS (
      SELECT 1
      FROM information_schema.columns
      WHERE table_schema = DATABASE()
        AND table_name = 'readers'
        AND column_name = 'card_code'
    ),
    'ALTER TABLE readers CHANGE COLUMN card_number card_code VARCHAR(30) NOT NULL',
    'SELECT 1'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    EXISTS (
      SELECT 1
      FROM information_schema.columns
      WHERE table_schema = DATABASE()
        AND table_name = 'readers'
        AND column_name = 'name'
    )
    AND NOT EXISTS (
      SELECT 1
      FROM information_schema.columns
      WHERE table_schema = DATABASE()
        AND table_name = 'readers'
        AND column_name = 'full_name'
    ),
    'ALTER TABLE readers CHANGE COLUMN name full_name VARCHAR(150) NOT NULL',
    'SELECT 1'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
  SELECT IF(
    NOT EXISTS (
      SELECT 1
      FROM information_schema.columns
      WHERE table_schema = DATABASE()
        AND table_name = 'readers'
        AND column_name = 'created_by_user_id'
    ),
    'ALTER TABLE readers ADD COLUMN created_by_user_id BIGINT NULL',
    'SELECT 1'
  )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE readers
  MODIFY COLUMN card_code VARCHAR(30) NOT NULL,
  MODIFY COLUMN full_name VARCHAR(150) NOT NULL,
  MODIFY COLUMN email VARCHAR(100) NULL,
  MODIFY COLUMN phone VARCHAR(20) NULL,
  MODIFY COLUMN address VARCHAR(255) NULL,
  MODIFY COLUMN card_status ENUM('ACTIVE','EXPIRED','LOCKED') NULL DEFAULT 'ACTIVE',
  MODIFY COLUMN card_issued_at DATE NULL,
  MODIFY COLUMN card_expiry_date DATE NULL,
  MODIFY COLUMN active TINYINT(1) NOT NULL DEFAULT 1,
  MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
