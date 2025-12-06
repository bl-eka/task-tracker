package com.tasktracker.repository;

import com.tasktracker.entity.Task;
import com.tasktracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Найти все задачи пользователя
    List<Task> findByUser(User user);
    
    // Найти задачу по ID и пользователю
    Optional<Task> findByIdAndUser(Long id, User user);
    
    // Найти задачи по статусу и пользователю
    List<Task> findByStatusAndUser(String status, User user);
}
