package com.tasktracker.service;  // Оставить как есть

import com.tasktracker.dto.TaskDto;
import com.tasktracker.entity.*;
import com.tasktracker.repository.TaskGroupRepository;
import com.tasktracker.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskService {  // Должен быть ТОЛЬКО ОДИН такой класс в файле!

    private final TaskRepository taskRepository;
    private final TaskGroupRepository taskGroupRepository;

    public List<TaskDto> getUserTasks(User user) {
        log.info("=== GET USER TASKS START ===");
        log.info("Getting tasks for user: {}", user.getEmail());
        log.info("User ID: {}", user.getId());

        List<Task> tasks = taskRepository.findByUser(user);
        log.info("Found {} tasks for user {}", tasks.size(), user.getEmail());

        tasks.forEach(task -> {
            log.info("Task: id={}, title={}, status={}, user_id={}",
                    task.getId(), task.getTitle(), task.getStatus(),
                    task.getUser() != null ? task.getUser().getId() : "null");
        });

        log.info("=== GET USER TASKS END ===");
        return tasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TaskDto getTaskById(Long id, User user) {
        log.info("Getting task by id={} for user: {}", id, user.getEmail());

        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        return convertToDto(task);
    }

    public TaskDto createTask(TaskDto taskDto, User user) {
        log.info("=== CREATE TASK START ===");
        log.info("Creating task for user: {}", user.getEmail());
        log.info("Task DTO: title={}, description={}, status={}, groupId={}",
                taskDto.getTitle(), taskDto.getDescription(),
                taskDto.getStatus(), taskDto.getGroupId());

        Task task = Task.builder()
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .status(taskDto.getStatus() != null ? taskDto.getStatus() : TaskStatus.PLANNED)
                .user(user)
                .build();

        if (taskDto.getGroupId() != null) {
            TaskGroup group = taskGroupRepository.findByIdAndUser(taskDto.getGroupId(), user)
                    .orElse(null);
            if (group != null) {
                task.setTaskGroup(group);
                log.info("Task assigned to group: {}", group.getName());
            } else {
                log.warn("Group not found: id={}", taskDto.getGroupId());
            }
        }

        log.info("Saving task...");
        Task savedTask = taskRepository.save(task);
        log.info("Task saved: id={}, title={}, user_id={}",
                savedTask.getId(), savedTask.getTitle(), savedTask.getUser().getId());

        // Проверяем, действительно ли задача сохранилась
        Task retrievedTask = taskRepository.findById(savedTask.getId()).orElse(null);
        log.info("Retrieved task after save: {}", retrievedTask != null ? "SUCCESS" : "FAILED");

        log.info("=== CREATE TASK END ===");
        return convertToDto(savedTask);
    }

    public TaskDto updateTask(Long id, TaskDto taskDto, User user) {
        log.info("Updating task id={} for user: {}", id, user.getEmail());

        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setStatus(taskDto.getStatus());

        if (taskDto.getGroupId() != null) {
            TaskGroup group = taskGroupRepository.findByIdAndUser(taskDto.getGroupId(), user)
                    .orElse(null);
            task.setTaskGroup(group);
        } else {
            task.setTaskGroup(null);
        }

        Task updatedTask = taskRepository.save(task);
        return convertToDto(updatedTask);
    }

    public TaskDto updateTaskStatus(Long id, TaskStatus status, User user) {
        log.info("Updating task status id={} to {} for user: {}", id, status, user.getEmail());

        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);

        return convertToDto(updatedTask);
    }

    public void deleteTask(Long id, User user) {
        log.info("Deleting task id={} for user: {}", id, user.getEmail());

        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task);

        log.info("Task deleted: id={}", id);
    }

    private TaskDto convertToDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .userId(task.getUser().getId())
                .groupId(task.getTaskGroup() != null ? task.getTaskGroup().getId() : null)
                .groupName(task.getTaskGroup() != null ? task.getTaskGroup().getName() : null)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
// Конец файла - здесь не должно быть больше классов!