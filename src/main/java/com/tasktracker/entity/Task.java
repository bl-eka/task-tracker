package com.tasktracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.PLANNED;

    // Связь с пользователем (владельцем задачи)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Связь с группой (ОБЯЗАТЕЛЬНАЯ по ТЗ, но optional = true)
    @ManyToOne
    @JoinColumn(name = "group_id")
    private TaskGroup group;  // может быть null (задача без группы)

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}