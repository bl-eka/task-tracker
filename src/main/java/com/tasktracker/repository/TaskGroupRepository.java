package com.tasktracker.repository;

import com.tasktracker.entity.TaskGroup;
import com.tasktracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskGroupRepository extends JpaRepository<TaskGroup, Long> {

    List<TaskGroup> findByUser(User user);

    Optional<TaskGroup> findByIdAndUser(Long id, User user);
}