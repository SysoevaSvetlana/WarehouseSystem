# Руководство по запуску и настройке

## Шаг 1: Запуск MySQL в Docker

### 1.1 Запустить контейнер MySQL

```bash
docker-compose up -d
```

Проверить, что контейнер запущен:
```bash
docker ps
```

Вы должны увидеть контейнер `warehouse_mysql`.

### 1.2 Проверить логи (опционально)

```bash
docker logs warehouse_mysql
```

## Шаг 2: Подключение к MySQL через DBeaver

### 2.1 Параметры подключения

Откройте DBeaver и создайте новое подключение к MySQL со следующими параметрами:

- **Host:** `localhost`
- **Port:** `3306`
- **Database:** `warehouse_db`
- **Username:** `root`
- **Password:** `Astra2005`

### 2.2 Тестирование подключения

Нажмите "Test Connection" в DBeaver. Если все настроено правильно, вы увидите сообщение об успешном подключении.

## Шаг 3: Запуск Backend (Spring Boot)

### 3.1 Запуск через Maven

```bash
mvn spring-boot:run
```

Или через Maven Wrapper (если Maven не установлен глобально):

**Windows:**
```bash
.\mvnw.cmd spring-boot:run
```

**Linux/Mac:**
```bash
./mvnw spring-boot:run
```

### 3.2 Проверка запуска

Backend запустится на порту **8080**. Вы должны увидеть в логах:
```
Started ProjectApplication in X.XXX seconds
```

Проверьте Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

## Шаг 4: Создание администратора

### Вариант 1: Через SQL скрипт в DBeaver

1. Откройте DBeaver
2. Подключитесь к базе данных `warehouse_db`
3. Откройте SQL редактор (SQL Editor)
4. Скопируйте содержимое файла `init-admin.sql`
5. Выполните скрипт (Ctrl+Enter или кнопка Execute)

Будут созданы два пользователя:
- **admin** / admin123 (ROLE_ADMIN)
- **storekeeper** / admin123 (ROLE_STOREKEEPER)

### Вариант 2: Через API (регистрация + изменение роли в БД)

1. Зарегистрируйте пользователя через API:

```bash
curl -X POST http://localhost:8080/api/auth/sign-up \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@warehouse.com",
    "password": "admin123"
  }'
```

2. Откройте DBeaver и выполните SQL:

```sql
UPDATE users 
SET role = 'ROLE_ADMIN' 
WHERE username = 'admin';
```

### Вариант 3: Через командную строку MySQL

```bash
docker exec -it warehouse_mysql mysql -uroot -pAstra2005 warehouse_db
```

Затем выполните SQL команды из `init-admin.sql`.

## Шаг 5: Запуск Frontend

### 5.1 Установка зависимостей (только первый раз)

```bash
cd frontend
npm install
```

### 5.2 Запуск dev сервера

```bash
npm run dev
```

Frontend будет доступен на: **http://localhost:3000**

## Шаг 6: Вход в систему

1. Откройте браузер: http://localhost:3000
2. Введите учетные данные:
   - **Username:** admin
   - **Password:** admin123
3. Нажмите "Войти"

## Заполнение базы данных вручную через DBeaver

### Создание складов

```sql
INSERT INTO warehouses (name, location) VALUES 
('Центральный склад', 'Москва, ул. Складская, 1'),
('Склад №2', 'Санкт-Петербург, пр. Невский, 100'),
('Региональный склад', 'Казань, ул. Баумана, 50');
```

### Создание товаров

```sql
INSERT INTO products (name, unit, description) VALUES 
('Ноутбук Dell XPS 15', 'шт', 'Ноутбук для офисной работы'),
('Монитор Samsung 27"', 'шт', 'Монитор Full HD'),
('Клавиатура Logitech', 'шт', 'Беспроводная клавиатура'),
('Мышь Logitech MX Master', 'шт', 'Беспроводная мышь'),
('Кабель HDMI 2м', 'шт', 'Кабель для подключения монитора');
```

### Создание поставок (через API или вручную)

Поставки лучше создавать через API или фронтенд, так как они связаны с пользователем и требуют правильной обработки остатков.

## Полезные SQL запросы для проверки

### Просмотр всех пользователей
```sql
SELECT id, username, email, role FROM users;
```

### Просмотр всех складов
```sql
SELECT * FROM warehouses;
```

### Просмотр всех товаров
```sql
SELECT * FROM products;
```

### Просмотр остатков на складах
```sql
SELECT 
    s.id,
    p.name as product_name,
    w.name as warehouse_name,
    s.count,
    s.last_update
FROM stock s
JOIN products p ON s.product_id = p.id
JOIN warehouses w ON s.warehouse_id = w.id
ORDER BY s.last_update DESC;
```

### Просмотр всех поставок
```sql
SELECT 
    sh.id,
    sh.transaction_type,
    sh.date,
    w.name as warehouse_name,
    u.username
FROM shipments sh
JOIN warehouses w ON sh.warehouse_id = w.id
JOIN users u ON sh.user_id = u.id
ORDER BY sh.date DESC;
```

## Остановка приложения

### Остановить Backend
Нажмите `Ctrl+C` в терминале, где запущен Spring Boot

### Остановить Frontend
Нажмите `Ctrl+C` в терминале, где запущен Vite

### Остановить MySQL контейнер
```bash
docker-compose down
```

Для полного удаления данных (включая volume):
```bash
docker-compose down -v
```

## Troubleshooting

### Порт 3306 уже занят
Если у вас уже установлен MySQL локально, измените порт в `docker-compose.yml`:
```yaml
ports:
  - "3307:3306"  # Используем 3307 вместо 3306
```

И в `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3307/warehouse_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

### Backend не может подключиться к БД
1. Проверьте, что MySQL контейнер запущен: `docker ps`
2. Проверьте логи: `docker logs warehouse_mysql`
3. Проверьте параметры подключения в `application.properties`

### Ошибка "Table doesn't exist"
Hibernate создаст таблицы автоматически при первом запуске (настройка `spring.jpa.hibernate.ddl-auto=create`).
Если таблицы не создались, проверьте логи Spring Boot.

