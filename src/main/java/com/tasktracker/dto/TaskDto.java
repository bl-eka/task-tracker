package com.tasktracker.dto;

import com.tasktracker.entity.TaskStatus;
import lombok.Data;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Long userId;
    private Long groupId;
    private String groupName;
}