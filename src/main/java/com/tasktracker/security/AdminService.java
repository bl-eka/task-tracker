package com.tasktracker.service;

// ИЗМЕНЕНО: UserDto и TaskDto вместо UserDTO и TaskDTO
import com.tasktracker.dto.UserDto;
import com.tasktracker.dto.TaskDto;
import com.tasktracker.entity.User;
import com.tasktracker.entity.Task;
import com.tasktracker.repository.UserRepository;
import com.tasktracker.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    // ИЗМЕНЕНО: UserDto вместо UserDTO
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    // ИЗМЕНЕНО: TaskDto вместо TaskDTO
    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::convertToTaskDTO)
                .collect(Collectors.toList());
    }

    // ИЗМЕНЕНО: TaskDto вместо TaskDTO
    public List<TaskDto> getTasksByUserId(Long userId) {
        return taskRepository.findByUserId(userId).stream()
                .map(this::convertToTaskDTO)
                .collect(Collectors.toList());
    }

    public Map<String, Long> getTasksByStatusStatistics() {
        List<Object[]> results = taskRepository.countTasksByStatus();
        return results.stream()
                .collect(Collectors.toMap(
                        obj -> obj[0].toString(),
                        obj -> (Long) obj[1]
                ));
    }

    public Map<String, Long> getTasksByGroupStatistics() {
        List<Object[]> results = taskRepository.countTasksByGroup();
        return results.stream()
                .collect(Collectors.toMap(
                        obj -> obj[0] != null ? obj[0].toString() : "Без группы",
                        obj -> (Long) obj[1]
                ));
    }

    public Map<String, Object> getSystemOverview() {
        long totalUsers = userRepository.count();
        long totalTasks = taskRepository.count();
        long totalGroups = taskRepository.countDistinctGroups();

        Map<String, Long> tasksByStatus = getTasksByStatusStatistics();
        Map<String, Long> tasksByGroup = getTasksByGroupStatistics();

        return Map.of(
                "totalUsers", totalUsers,
                "totalTasks", totalTasks,
                "totalGroups", totalGroups,
                "tasksByStatus", tasksByStatus,
                "tasksByGroup", tasksByGroup
        );
    }

    // ИЗМЕНЕНО: UserDto вместо UserDTO
    private UserDto convertToUserDTO(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name()); // Добавьте это
        // Удалите это, если в UserDto нет createdAt
        // dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    // ИЗМЕНЕНО: TaskDto вместо TaskDTO
    private TaskDto convertToTaskDTO(Task task) {
        // Используем билдер, который есть в TaskDto
        TaskDto.TaskDtoBuilder builder = TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .userId(task.getUser().getId());

        if (task.getTaskGroup() != null) {
            builder.groupId(task.getTaskGroup().getId())
                    .groupName(task.getTaskGroup().getName());
        }

        // Если в Task есть поля createdAt и updatedAt, раскомментируйте:
        // builder.createdAt(task.getCreatedAt());
        // builder.updatedAt(task.getUpdatedAt());

        return builder.build();
    }
}