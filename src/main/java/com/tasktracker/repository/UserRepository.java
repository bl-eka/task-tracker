package com.tasktracker.repository;

import com.tasktracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // ✅ Этот метод должен быть!
    Optional<User> findByEmail(String email);
}