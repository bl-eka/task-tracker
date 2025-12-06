package com.tasktracker.controller;

import com.tasktracker.dto.TaskGroupDto;
import com.tasktracker.entity.User;
import com.tasktracker.service.TaskGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Tag(name = "Группы задач", description = "Управление группами задач")
public class TaskGroupController {

    private final TaskGroupService taskGroupService;

    @Operation(summary = "Получить все группы пользователя")
    @GetMapping
    public ResponseEntity<List<TaskGroupDto>> getUserGroups(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskGroupService.getUserGroups(user));
    }

    @Operation(summary = "Создать новую группу")
    @PostMapping
    public ResponseEntity<TaskGroupDto> createGroup(
            @RequestBody TaskGroupDto groupDto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskGroupService.createGroup(groupDto, user));
    }

    @Operation(summary = "Переименовать группу")
    @PutMapping("/{id}")
    public ResponseEntity<TaskGroupDto> renameGroup(
            @PathVariable Long id,
            @RequestBody TaskGroupDto groupDto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskGroupService.renameGroup(id, groupDto, user));
    }

    @Operation(summary = "Удалить группу")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        taskGroupService.deleteGroup(id, user);
        return ResponseEntity.noContent().build();
    }
}