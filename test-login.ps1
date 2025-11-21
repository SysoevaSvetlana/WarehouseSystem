# Тест входа через API

Write-Host "Тестирование входа в систему..." -ForegroundColor Cyan
Write-Host ""

# Тест 1: Проверка доступности API
Write-Host "1. Проверка доступности бэкенда..." -ForegroundColor Yellow
try {
    $null = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/sign-in" -Method POST -ErrorAction Stop
    Write-Host "OK Бэкенд доступен" -ForegroundColor Green
}
catch {
    if ($_.Exception.Response.StatusCode -eq 400 -or $_.Exception.Response.StatusCode -eq 401) {
        Write-Host "OK Бэкенд доступен (получен ответ от сервера)" -ForegroundColor Green
    }
    else {
        Write-Host "X Бэкенд недоступен на http://localhost:8080" -ForegroundColor Red
        Write-Host "Убедитесь, что Spring Boot приложение запущено" -ForegroundColor Yellow
        exit 1
    }
}

Write-Host ""

# Тест 2: Попытка входа
Write-Host "2. Попытка входа с учетными данными admin/admin123..." -ForegroundColor Yellow

$body = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/sign-in" `
        -Method POST `
        -ContentType "application/json" `
        -Body $body

    Write-Host "OK Вход успешен!" -ForegroundColor Green
    Write-Host "Токен получен" -ForegroundColor Green
}
catch {
    Write-Host "X Ошибка входа" -ForegroundColor Red

    if ($_.Exception.Response) {
        try {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $responseBody = $reader.ReadToEnd()
            Write-Host "Ответ сервера: $responseBody" -ForegroundColor Red
        }
        catch {
            Write-Host "Не удалось прочитать ответ сервера" -ForegroundColor Red
        }
    }

    Write-Host ""
    Write-Host "Возможные причины:" -ForegroundColor Yellow
    Write-Host "  - Пользователь не создан в БД (выполните init-admin.sql)" -ForegroundColor Yellow
    Write-Host "  - Неверный пароль" -ForegroundColor Yellow
    Write-Host "  - Проблема с бэкендом" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Для создания пользователя выполните SQL в DBeaver (см. файл init-admin.sql)" -ForegroundColor Cyan

