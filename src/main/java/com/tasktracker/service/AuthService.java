package com.tasktracker.service;

import com.tasktracker.dto.AuthRequest;
import com.tasktracker.dto.AuthResponse;
import com.tasktracker.entity.Role;
import com.tasktracker.entity.User;
import com.tasktracker.repository.UserRepository;
import com.tasktracker.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(AuthRequest request) {
        log.info("Registering user: {}", request.getEmail());

        // Проверяем, существует ли пользователь
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        // Создаем пользователя (без builder, используем конструктор и сеттеры)
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER); // Используем ROLE_USER вместо USER

        userRepository.save(user);
        log.info("User registered: {} (ID: {})", user.getEmail(), user.getId());

        // Создаем UserDetails
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()))
                // Обратите внимание: user.getRole().name() вернет "ROLE_USER"
        );

        // Генерируем токен
        String token = jwtService.generateToken(userDetails);
        log.info("Token generated for: {}", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        log.info("Login attempt for user: {}", request.getEmail());

        // Находим пользователя
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Проверяем пароль
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        log.info("Login successful for: {}", user.getEmail());

        // Создаем UserDetails
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()))
        );

        // Генерируем токен
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .build();
    }
}