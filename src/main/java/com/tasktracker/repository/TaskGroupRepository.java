package com.tasktracker.repository;

import com.tasktracker.entity.TaskGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskGroupRepository extends JpaRepository<TaskGroup, Long> {
}