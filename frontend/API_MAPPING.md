# Согласование ТЗ с Backend API

## Основные отличия от ТЗ

### 1. Роли пользователей
**ТЗ:** ADMIN, USER, OPERATOR  
**Backend:** `ROLE_ADMIN`, `ROLE_STOREKEEPER`  
**Решение:** Используем роли из бэкенда

### 2. Типы операций (Shipments)
**ТЗ:** IN, OUT  
**Backend:** `incoming`, `write-off`, `outgoing`, `transfer`  
**Решение:** 
- `incoming` - приход товара на склад
- `write-off` - списание товара со склада
- `transfer` - перемещение между складами (не реализовано в UI, но API поддерживает)

### 3. Аутентификация
**Backend:** JWT токены через `/api/auth/sign-in` и `/api/auth/sign-up`  
**Frontend:** Токен сохраняется в localStorage, добавляется в заголовок Authorization

## Маппинг страниц на API

### Shipments (Поставки)
- **GET /api/shipments** - Получить список поставок
  - Параметры: `transactionType`, `warehouseId`, `fromDate`, `toDate`, `page`, `size`
- **POST /api/shipments/incoming** - Создать приход
  - Body: `{ warehouseId, items: [{ productId, count }] }`
- **POST /api/shipments/write-off** - Создать списание
  - Body: `{ warehouseId, items: [{ productId, count }] }`
- **POST /api/shipments/transfer** - Создать перемещение
  - Body: `{ fromWarehouseId, toWarehouseId, items: [{ productId, count }] }`

### Warehouses (Склады)
- **GET /api/warehouses** - Получить список складов
  - Параметры: `name`, `page`, `size`
- **POST /api/warehouses** - Создать склад (только ADMIN)
  - Body: `{ name, location }`
- **PUT /api/warehouses/{id}** - Обновить склад (только ADMIN)
- **DELETE /api/warehouses/{id}** - Удалить склад (только ADMIN)

### Stock (Остатки)
- **GET /api/stock** - Получить остатки
  - Параметры: `productName`, `warehouseId`, `page`, `size`
- Только чтение (read-only)

### Products (Товары)
- **GET /api/products** - Получить список товаров
  - Параметры: `name`, `page`, `size`
- **POST /api/products** - Создать товар
  - Body: `{ name, unit, description }`
- **PUT /api/products/{id}** - Обновить товар
- **DELETE /api/products/{id}** - Удалить товар

### Users (Пользователи)
- **GET /api/users** - Получить список пользователей (только ADMIN)
  - Параметры: `page`, `size`
- **PATCH /api/users/{id}/role** - Изменить роль (только ADMIN)
  - Body: `{ role: "ROLE_ADMIN" | "ROLE_STOREKEEPER" }`
- **DELETE /api/users/{id}** - Удалить пользователя (только ADMIN)

## Структура данных

### ShipmentDto
```json
{
  "id": 1,
  "transactionType": "incoming",
  "date": "2024-01-15",
  "warehouse": { "id": 1, "name": "Центральный склад", "location": "..." },
  "user": { "id": 1, "username": "admin", "email": "...", "role": "ROLE_ADMIN" },
  "items": [
    {
      "id": 1,
      "count": 50,
      "productId": 1,
      "product": { "id": 1, "name": "Товар", "unit": "шт", "description": "..." }
    }
  ]
}
```

### StockDto
```json
{
  "id": 1,
  "count": 100,
  "lastUpdate": "2024-01-15T10:30:00",
  "warehouse": { "id": 1, "name": "...", "location": "..." },
  "product": { "id": 1, "name": "...", "unit": "...", "description": "..." }
}
```

## Права доступа

### ROLE_ADMIN
- Полный доступ ко всем функциям
- Управление складами (создание, редактирование, удаление)
- Управление пользователями
- Все операции с товарами и поставками

### ROLE_STOREKEEPER
- Просмотр и создание поставок
- Просмотр складов (без редактирования)
- Управление товарами
- Просмотр остатков
- Нет доступа к управлению пользователями

## Обработка ошибок

- **401 Unauthorized** - Автоматический выход из системы
- **403 Forbidden** - Недостаточно прав (показывается alert)
- **404 Not Found** - Ресурс не найден
- **400 Bad Request** - Ошибка валидации (показывается сообщение из response)

