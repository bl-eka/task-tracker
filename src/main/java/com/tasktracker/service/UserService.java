package com.tasktracker.service;

import com.tasktracker.entity.User;
import com.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // ✅ ДОБАВИТЬ ЭТОТ МЕТОД
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // ✅ ДОБАВИТЬ ЭТОТ МЕТОД (или оставить только один)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
}