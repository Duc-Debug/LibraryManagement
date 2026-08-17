-- Migration: V1_4__create_file_cleanup_tasks_table.sql
-- Description: Create table for transactional outbox file cleanup queue

CREATE TABLE file_cleanup_tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_url VARCHAR(1000) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    retry_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_file_cleanup_status_created ON file_cleanup_tasks(status, created_at);
