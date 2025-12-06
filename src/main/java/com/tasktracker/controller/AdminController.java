package com.tasktracker.controller;

import com.tasktracker.service.AdminService;  // Убедитесь, что импорт из service!
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Администратор", description = "Эндпоинты для администраторов системы")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    @Operation(summary = "Получить список всех пользователей")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/tasks")
    @Operation(summary = "Получить все задачи в системе")
    public ResponseEntity<?> getAllTasks() {
        return ResponseEntity.ok(adminService.getAllTasks());
    }

    @GetMapping("/tasks/user/{userId}")
    @Operation(summary = "Получить задачи конкретного пользователя")
    public ResponseEntity<?> getUserTasks(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getTasksByUserId(userId));
    }

    @GetMapping("/statistics/tasks-by-status")
    @Operation(summary = "Статистика задач по статусам")
    public ResponseEntity<?> getTasksByStatusStatistics() {
        return ResponseEntity.ok(adminService.getTasksByStatusStatistics());
    }

    @GetMapping("/statistics/tasks-by-group")
    @Operation(summary = "Статистика задач по группам")
    public ResponseEntity<?> getTasksByGroupStatistics() {
        return ResponseEntity.ok(adminService.getTasksByGroupStatistics());
    }

    @GetMapping("/statistics/overview")
    @Operation(summary = "Общая статистика системы")
    public ResponseEntity<?> getSystemOverview() {
        Map<String, Object> statistics = adminService.getSystemOverview();
        return ResponseEntity.ok(statistics);
    }
}