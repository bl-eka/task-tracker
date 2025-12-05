@echo off
echo =========================================
echo   ЗАПУСК TASK TRACKER (Spring Boot)
echo =========================================
echo.

echo [1/3] Проверка порта 8083...
netstat -ano | findstr :8083 > nul

if %errorlevel% equ 0 (
    echo    Порт 8083 занят. Останавливаю процесс...
    for /f "tokens=5" %%i in ('netstat -ano ^| findstr :8083 ^| findstr LISTENING') do (
        echo    Найден процесс PID: %%i
        taskkill /PID %%i /F
    )
    timeout /t 2 /nobreak > nul
    echo    ✓ Процесс остановлен
) else (
    echo    ✓ Порт 8083 свободен
)

echo.
echo [2/3] Проверка Docker контейнера...
docker ps --filter "name=task-tracker-db" --format "{{.Names}}" | findstr task-tracker-db > nul

if %errorlevel% equ 0 (
    echo    ✓ PostgreSQL контейнер запущен
) else (
    echo    ⚠ PostgreSQL контейнер не запущен
    echo    Запускаю docker-compose...
    docker-compose up -d
    timeout /t 5 /nobreak > nul
    echo    ✓ Docker контейнер запущен
)

echo.
echo [3/3] Запуск Spring Boot приложения...
echo    Порт: 8083
echo    БД: PostgreSQL (5433)
echo    Swagger: http://localhost:8083/swagger-ui.html
echo =========================================
echo.

call mvnw.cmd spring-boot:run -DskipTests

pause