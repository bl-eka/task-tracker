package com.tasktracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskGroupDto {
    private Long id;
    private String name;
    private Long userId;
    private Integer taskCount;
}