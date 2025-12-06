@echo off
chcp 65001 > nul
SETLOCAL

echo 🔧 Настройка Java 17 для проекта...

:: Поиск Java 17 в стандартных папках
set JAVA17_FOUND=
set SEARCH_PATHS="C:\Program Files\Java" "C:\Program Files\Eclipse Adoptium" "C:\Program Files\AdoptOpenJDK"

for %%P in (%SEARCH_PATHS%) do (
    if exist %%P (
        for /f "delims=" %%J in ('dir "%%P\jdk*17*" /b /ad 2^>nul') do (
            set "JAVA17_FOUND=%%P\%%J"
            goto :FOUND_JAVA
        )
    )
)

:FOUND_JAVA
if "%JAVA17_FOUND%"=="" (
    echo ❌ Java 17 не найдена!
    echo Установите Java 17 с: https://adoptium.net/temurin/releases/?version=17
    pause
    exit /b 1
)

echo ✅ Найдена Java 17: %JAVA17_FOUND%
echo.

:: Устанавливаем переменные
set "JAVA_HOME=%JAVA17_FOUND%"
set "PATH=%JAVA_HOME%\bin;%PATH%"

:: Проверка Java
echo --- Проверка ---
java -version
echo ---

if errorlevel 1 (
    echo ❌ Ошибка при проверке Java
    pause
    exit /b 1
)

echo ✅ Java настроена успешно!
echo.
echo Теперь можно:
echo 1. Скомпилировать: .\mvnw.cmd clean compile
echo 2. Запустить: .\start-app.bat
echo.

pause