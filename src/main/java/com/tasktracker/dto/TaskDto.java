package com.tasktracker.dto;

import com.tasktracker.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Long userId;
    private Long groupId;  // ИЗМЕНИЛ: было taskGroupId
    private String groupName;  // ДОБАВИЛ
    private LocalDateTime createdAt;  // ДОБАВИЛ
    private LocalDateTime updatedAt;  // ДОБАВИЛ
}