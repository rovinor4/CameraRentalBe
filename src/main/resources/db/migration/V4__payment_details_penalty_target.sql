DELIMITER //

CREATE PROCEDURE migrate_payment_details_penalty_target()
BEGIN
    ALTER TABLE rental_payments
        MODIFY rental_id BIGINT NULL;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'rental_payments'
          AND COLUMN_NAME = 'penalty_id'
    ) THEN
        ALTER TABLE rental_payments
            ADD COLUMN penalty_id BIGINT NULL AFTER rental_id;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'rental_payments'
          AND INDEX_NAME = 'idx_rental_payments_penalty_id'
    ) THEN
        CREATE INDEX idx_rental_payments_penalty_id ON rental_payments (penalty_id);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.KEY_COLUMN_USAGE
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'rental_payments'
          AND COLUMN_NAME = 'penalty_id'
          AND REFERENCED_TABLE_NAME = 'penalties'
    ) THEN
        ALTER TABLE rental_payments
            ADD CONSTRAINT fk_rental_payments_penalty FOREIGN KEY (penalty_id) REFERENCES penalties(id) ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.TABLE_CONSTRAINTS
        WHERE CONSTRAINT_SCHEMA = DATABASE()
          AND TABLE_NAME = 'rental_payments'
          AND CONSTRAINT_NAME = 'chk_rental_payments_target'
    ) THEN
        ALTER TABLE rental_payments
            ADD CONSTRAINT chk_rental_payments_target CHECK (
                (rental_id IS NOT NULL AND penalty_id IS NULL)
                OR (rental_id IS NULL AND penalty_id IS NOT NULL)
            );
    END IF;
END//

CALL migrate_payment_details_penalty_target()//

DROP PROCEDURE migrate_payment_details_penalty_target//

DELIMITER ;
