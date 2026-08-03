USE library_management;

-- Xóa user admin cũ nếu bị lỗi
DELETE FROM users WHERE username = 'admin';

-- Thêm user admin mới với password '123456' đã hash BCrypt
INSERT INTO users (username, password_hash, full_name, email, phone, enabled)
VALUES (
    'admin', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 
    'System Administrator', 
    'admin@library.com', 
    '0987654321', 
    1
);

-- Gán quyền ADMIN (Giả định id trong bảng roles của ADMIN là 1)
INSERT INTO user_roles (user_id, role_id)
VALUES (LAST_INSERT_ID(), 1);