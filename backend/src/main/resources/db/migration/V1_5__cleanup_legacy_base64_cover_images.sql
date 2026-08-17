-- Migration: V1_5__cleanup_legacy_base64_cover_images.sql
-- Description: Clean up legacy Base64 cover images in database

UPDATE books
SET cover_image_url = NULL
WHERE cover_image_url LIKE 'data:%';
