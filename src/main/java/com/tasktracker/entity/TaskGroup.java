package com.tasktracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "task_groups")
@Data
@NoArgsConstructor
public class TaskGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Связь с пользователем (владельцем группы)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Связь с задачами в группе
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<Task> tasks = new ArrayList<>();
}