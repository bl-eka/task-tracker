package com.tasktracker.repository;

import com.tasktracker.entity.Task;
import com.tasktracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    // Найти задачи по ID пользователя
    List<Task> findByUserId(Long userId);

    // JPQL запрос для статистики по статусам (требование проекта)
    @Query("SELECT t.status, COUNT(t) FROM Task t GROUP BY t.status")
    List<Object[]> countTasksByStatus();

    // JPQL запрос для статистики по группам (требование проекта)
    @Query("SELECT tg.name, COUNT(t) FROM Task t LEFT JOIN t.taskGroup tg GROUP BY tg.name")
    List<Object[]> countTasksByGroup();

    // Подсчет уникальных групп
    @Query("SELECT COUNT(DISTINCT t.taskGroup) FROM Task t WHERE t.taskGroup IS NOT NULL")
    Long countDistinctGroups();
}