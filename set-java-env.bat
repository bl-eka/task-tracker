@echo off
chcp 65001 > nul

echo 🚀 Настройка Java 17 для Maven...

:: Найти Java 17
set JAVA17_PATH=
for /f "delims=" %%i in ('dir "C:\Program Files\Eclipse Adoptium\jdk-17*" /b /ad 2^>nul') do (
    set "JAVA17_PATH=C:\Program Files\Eclipse Adoptium\%%i"
)

if "%JAVA17_PATH%"=="" (
    echo ❌ Java 17 не найдена в стандартной папке.
    echo Поиск в других местах...
    for /f "delims=" %%i in ('dir "C:\Program Files\Java\jdk-17*" /b /ad 2^>nul') do (
        set "JAVA17_PATH=C:\Program Files\Java\%%i"
    )
)

if "%JAVA17_PATH%"=="" (
    echo ❌ Java 17 не найдена.
    echo Установленная Java:
    java -version
    pause
    exit /b 1
)

echo ✅ Найдена Java 17: %JAVA17_PATH%

:: Установить переменные
set "JAVA_HOME=%JAVA17_PATH%"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo --- Проверка ---
java -version
echo.

echo Теперь Maven будет использовать Java 17.
pause