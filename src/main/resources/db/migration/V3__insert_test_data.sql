-- Добавим пользователей
INSERT INTO users (name, date_of_birth, password)
VALUES
    ('Иван Иванов', '1990-01-01', '$2a$12$HTvLN/GS359RJtq4eExxreQu5WXjsjbAembbkOvwc.VB/smAMbeTq'), --123456789
    ('Мария Смирнова', '1985-05-20', '$2a$12$Nsjc1YWgHmjo32kGPTkZUeWk1fjqgrGsjvnElbGZGy/V4X7Wqg22K');--qwertyuio

-- Добавим аккаунты с балансом и initial_deposit
INSERT INTO account ( user_id, balance, initial_deposit)
VALUES
    ( 1, 100.00, 100.00),
    ( 2, 250.00, 250.00);

-- Добавим email-адреса
INSERT INTO email_data ( user_id, email)
VALUES
    ( 1, 'ivan@example.com'),
    ( 1, 'ivan.work@example.com'),
    ( 2, 'maria@example.com');

-- Добавим номера телефонов
INSERT INTO phone_data ( user_id, phone)
VALUES
    ( 1, '79207865432'),
    ( 1, '37468362836'),
    ( 2, '93039347476');
