package com.tasktracker.controller;

import com.tasktracker.dto.TaskDto;
import com.tasktracker.entity.TaskStatus;
import com.tasktracker.entity.User;
import com.tasktracker.service.TaskService;
import com.tasktracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Задачи", description = "Управление задачами")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService; // Добавляем UserService

    private User getUserFromDetails(UserDetails userDetails) {
        // Получаем User по email из UserDetails
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + userDetails.getUsername()));
    }

    @Operation(summary = "Получить все задачи пользователя")
    @GetMapping
    public ResponseEntity<List<TaskDto>> getUserTasks(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromDetails(userDetails);
        return ResponseEntity.ok(taskService.getUserTasks(user));
    }

    @Operation(summary = "Получить задачу по ID")
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromDetails(userDetails);
        return ResponseEntity.ok(taskService.getTaskById(id, user));
    }

    @Operation(summary = "Создать новую задачу")
    @PostMapping
    public ResponseEntity<TaskDto> createTask(
            @RequestBody TaskDto taskDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromDetails(userDetails);
        return ResponseEntity.ok(taskService.createTask(taskDto, user));
    }

    @Operation(summary = "Обновить задачу")
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable Long id,
            @RequestBody TaskDto taskDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromDetails(userDetails);
        return ResponseEntity.ok(taskService.updateTask(id, taskDto, user));
    }

    @Operation(summary = "Обновить статус задачи")
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskDto> updateTaskStatus(
            @PathVariable Long id,
            @RequestBody TaskStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromDetails(userDetails);
        return ResponseEntity.ok(taskService.updateTaskStatus(id, status, user));
    }

    @Operation(summary = "Удалить задачу")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromDetails(userDetails);
        taskService.deleteTask(id, user);
        return ResponseEntity.noContent().build();
    }
}