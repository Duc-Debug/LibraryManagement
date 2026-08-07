ALTER TABLE readers
ADD CONSTRAINT uk_readers_email UNIQUE (email);

ALTER TABLE readers
ADD CONSTRAINT uk_readers_phone UNIQUE (phone);