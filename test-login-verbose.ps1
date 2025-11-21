$body = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

Write-Host "Отправка запроса на http://localhost:8080/api/auth/sign-in" -ForegroundColor Cyan
Write-Host "Body: $body" -ForegroundColor Yellow

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/sign-in" `
        -Method Post `
        -Body $body `
        -ContentType "application/json" `
        -UseBasicParsing

    Write-Host "`nStatus Code: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "Response:" -ForegroundColor Green
    Write-Host $response.Content
} catch {
    Write-Host "`nError occurred:" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
    Write-Host "Status Description: $($_.Exception.Response.StatusDescription)" -ForegroundColor Red
    
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response Body: $responseBody" -ForegroundColor Red
    }
}

