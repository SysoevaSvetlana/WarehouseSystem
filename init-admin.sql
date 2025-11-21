-- Скрипт для создания администратора
-- Пароль: admin123 (BCrypt hash)

USE warehouse_db;

-- Создание администратора
INSERT INTO users (username, email, password, role) 
VALUES (
    'admin',
    'admin@warehouse.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',  -- admin123
    'ROLE_ADMIN'
);

-- Создание обычного пользователя (кладовщика)
INSERT INTO users (username, email, password, role) 
VALUES (
    'storekeeper',
    'storekeeper@warehouse.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',  -- admin123
    'ROLE_STOREKEEPER'
);

-- Проверка созданных пользователей
SELECT id, username, email, role FROM users;

