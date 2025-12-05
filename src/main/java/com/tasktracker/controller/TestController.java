package com.tasktracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Тестовый контроллер", description = "Проверка работы приложения")
public class TestController {

    @Operation(summary = "Проверка работы приложения",
            description = "Возвращает текущее время сервера")
    @GetMapping("/ping")
    public String ping() {
        return "✅ ПРИЛОЖЕНИЕ РАБОТАЕТ! Java 17, порт 8083, время: " + System.currentTimeMillis();
    }

    @Operation(summary = "Вторая проверка",
            description = "Альтернативный тестовый эндпоинт")
    @GetMapping("/ping2")
    public String pingNoApi() {
        return "✅ РАБОТАЕТ (второй тест)! Время: " + System.currentTimeMillis();
    }
}