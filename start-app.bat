@echo off
chcp 65001 > nul
SETLOCAL

echo =========================================
echo   ЗАПУСК TASK TRACKER (Spring Boot)
echo =========================================

echo [1/3] Проверка порта 8083...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8083') do (
    echo    ⚠️ Порт 8083 занят, останавливаем процесс PID: %%a
    taskkill /F /PID %%a 2>nul
)
echo    ✅ Порт 8083 свободен
echo.

echo [2/3] Проверка Docker контейнера...
docker-compose up -d
timeout /t 2 /nobreak > nul
echo    ✅ PostgreSQL контейнер запущен
echo.

echo [3/3] Запуск Spring Boot приложения...
echo    Порт: 8083
echo    БД: PostgreSQL (5433)
echo    Swagger: http://localhost:8083/swagger-ui.html
echo =========================================
echo.

:: Настройка Java 17
set JAVA17_PATH=

:: Поиск Java 17
for /f "delims=" %%i in ('dir "C:\Program Files\Eclipse Adoptium\jdk-17*" /b /ad 2^>nul') do (
    set "JAVA17_PATH=C:\Program Files\Eclipse Adoptium\%%i"
)

if "%JAVA17_PATH%"=="" (
    for /f "delims=" %%i in ('dir "C:\Program Files\Java\jdk-17*" /b /ad 2^>nul') do (
        set "JAVA17_PATH=C:\Program Files\Java\%%i"
    )
)

if not "%JAVA17_PATH%"=="" (
    set "JAVA_HOME=%JAVA17_PATH%"
    set "PATH=%JAVA_HOME%\bin;%PATH%"
    echo ✅ Используется Java 17: %JAVA_HOME%
) else (
    echo ⚠️ Java 17 не найдена, используется системная Java
    java -version
)

echo.
echo Запуск Spring Boot...
echo.

:: Запуск приложения
call mvnw.cmd spring-boot:run -DskipTests

pause