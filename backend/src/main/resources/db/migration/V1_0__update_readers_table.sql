-- Flyway Script: Quản lý thay đổi Schema DB độc lập, an toàn và có phiên bản rõ ràng

-- Nới lỏng/dọn dẹp các cột cũ nếu đã tồn tại
ALTER TABLE readers MODIFY COLUMN card_number VARCHAR(50) NULL;
ALTER TABLE readers MODIFY COLUMN name VARCHAR(100) NULL;

-- Đảm bảo có cột created_by_user_id
ALTER TABLE readers ADD COLUMN IF NOT EXISTS created_by_user_id BIGINT NULL;
