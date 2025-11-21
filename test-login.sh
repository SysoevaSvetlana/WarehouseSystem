#!/bin/bash

# Тест входа через API

echo "Тестирование входа в систему..."
echo ""

# Тест 1: Проверка доступности API
echo "1. Проверка доступности бэкенда..."
curl -s http://localhost:8080/api/auth/sign-in > /dev/null
if [ $? -eq 0 ]; then
    echo "✓ Бэкенд доступен"
else
    echo "✗ Бэкенд недоступен на http://localhost:8080"
    exit 1
fi

echo ""

# Тест 2: Попытка входа
echo "2. Попытка входа с учетными данными admin/admin123..."
response=$(curl -s -w "\n%{http_code}" -X POST http://localhost:8080/api/auth/sign-in \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }')

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n-1)

echo "HTTP код: $http_code"
echo "Ответ: $body"

if [ "$http_code" = "200" ]; then
    echo "✓ Вход успешен!"
    echo "Токен получен"
else
    echo "✗ Ошибка входа"
    echo "Возможные причины:"
    echo "  - Пользователь не создан в БД"
    echo "  - Неверный пароль"
    echo "  - Проблема с бэкендом"
fi

