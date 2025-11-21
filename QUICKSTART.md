# Быстрый старт - Складской учет

## Запуск приложения

### 1. Запуск Backend (Spring Boot)

```bash
# Из корневой директории проекта
mvn spring-boot:run
```

Backend будет доступен на: http://localhost:8080

### 2. Запуск Frontend (React + Vite)

```bash
# Перейти в директорию frontend
cd frontend

# Установить зависимости (только первый раз)
npm install

# Запустить dev сервер
npm run dev
```

Frontend будет доступен на: http://localhost:3000

## Первый вход

### Создание администратора

Используйте регистрацию через API или создайте пользователя напрямую в БД.

**Через API (Postman/curl):**
```bash
curl -X POST http://localhost:8080/api/auth/sign-up \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@example.com",
    "password": "admin123"
  }'
```

Затем измените роль пользователя в БД на `ROLE_ADMIN`.

### Вход в систему

1. Откройте http://localhost:3000
2. Введите логин и пароль
3. Вы будете перенаправлены на страницу Shipments

## Структура приложения

```
project/
├── src/main/java/          # Backend (Spring Boot)
│   └── warehouses/project/
│       ├── controller/     # REST контроллеры
│       ├── service/        # Бизнес-логика
│       ├── model/          # Сущности БД
│       └── dto/            # DTO объекты
│
└── frontend/               # Frontend (React)
    ├── src/
    │   ├── components/     # UI компоненты
    │   ├── pages/          # Страницы
    │   └── services/       # API сервисы
    └── package.json
```

## Основные функции

### Для всех пользователей (STOREKEEPER + ADMIN)
- ✅ Просмотр и создание поставок
- ✅ Просмотр остатков на складах
- ✅ Управление товарами
- ✅ Просмотр складов

### Только для ADMIN
- ✅ Управление складами (создание, удаление)
- ✅ Управление пользователями
- ✅ Изменение ролей пользователей

## Swagger UI

Backend предоставляет Swagger UI для тестирования API:

http://localhost:8080/swagger-ui.html

## Технологии

**Backend:**
- Java 17
- Spring Boot 3
- Spring Security (JWT)
- PostgreSQL / H2
- Swagger/OpenAPI

**Frontend:**
- React 18
- React Router 6
- Axios
- Vite

## Порты

- Backend: 8080
- Frontend: 3000
- Database: 5432 (PostgreSQL) или встроенная H2

## Troubleshooting

### Frontend не может подключиться к Backend
- Убедитесь, что Backend запущен на порту 8080
- Проверьте настройки прокси в `frontend/vite.config.js`

### Ошибка 401 при запросах
- Проверьте, что токен сохранен в localStorage
- Попробуйте выйти и войти заново

### CORS ошибки
- Backend должен разрешать запросы с localhost:3000
- Проверьте конфигурацию CORS в Spring Security

