
ALTER TABLE account
    ADD COLUMN initial_deposit NUMERIC;

UPDATE account
SET initial_deposit = balance
WHERE initial_deposit IS NULL;
