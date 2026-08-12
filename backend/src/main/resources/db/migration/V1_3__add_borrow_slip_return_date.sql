ALTER TABLE borrow_slips
    ADD COLUMN return_date DATETIME NULL AFTER due_at;