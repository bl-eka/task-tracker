package com.tasktracker.service;

import com.tasktracker.dto.AuthRequest;
import com.tasktracker.dto.AuthResponse;
import com.tasktracker.entity.Role;
import com.tasktracker.entity.User;
import com.tasktracker.repository.UserRepository;
import com.tasktracker.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class SimpleAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(AuthRequest request) {
        System.out.println("=== SIMPLE AUTH SERVICE REGISTER: " + request.getEmail() + " ===");

        // Проверка существования
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        // Создание пользователя
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);

        // Сохранение
        userRepository.save(user);
        System.out.println("User saved with ID: " + user.getId());

        // Создание UserDetails для JWT
        var userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(() -> "ROLE_USER")
        );

        // Генерация токена
        String token = jwtService.generateToken(userDetails);
        System.out.println("Token generated, length: " + token.length());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        System.out.println("=== SIMPLE AUTH SERVICE LOGIN: " + request.getEmail() + " ===");

        // Находим пользователя
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Проверяем пароль
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Создание UserDetails для JWT
        var userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(() -> user.getRole().name())
        );

        // Генерация токена
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .build();
    }
}