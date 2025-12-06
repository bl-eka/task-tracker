package com.tasktracker.service;

import com.tasktracker.dto.TaskGroupDto;
import com.tasktracker.entity.TaskGroup;
import com.tasktracker.entity.User;
import com.tasktracker.repository.TaskGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskGroupService {

    private final TaskGroupRepository taskGroupRepository;

    public List<TaskGroupDto> getUserGroups(User user) {
        log.info("Getting groups for user: {}", user.getEmail());
        return taskGroupRepository.findByUser(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TaskGroupDto createGroup(TaskGroupDto groupDto, User user) {
        log.info("Creating group for user: {}", user.getEmail());

        TaskGroup group = new TaskGroup();
        group.setName(groupDto.getName());
        group.setUser(user);

        TaskGroup savedGroup = taskGroupRepository.save(group);
        log.info("Group saved: id={}, name={}", savedGroup.getId(), savedGroup.getName());

        return convertToDto(savedGroup);
    }

    public TaskGroupDto renameGroup(Long id, TaskGroupDto groupDto, User user) {
        log.info("Renaming group id={} for user: {}", id, user.getEmail());

        TaskGroup group = taskGroupRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        group.setName(groupDto.getName());
        TaskGroup updatedGroup = taskGroupRepository.save(group);

        return convertToDto(updatedGroup);
    }

    public void deleteGroup(Long id, User user) {
        log.info("Deleting group id={} for user: {}", id, user.getEmail());

        TaskGroup group = taskGroupRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        taskGroupRepository.delete(group);

        log.info("Group deleted: id={}", id);
    }

    private TaskGroupDto convertToDto(TaskGroup group) {
        return TaskGroupDto.builder()
                .id(group.getId())
                .name(group.getName())
                .userId(group.getUser().getId())
                .taskCount(group.getTasks() != null ? group.getTasks().size() : 0)
                .build();
    }
}