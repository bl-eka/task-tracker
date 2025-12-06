package com.tasktracker.service;

import com.tasktracker.dto.AuthRequest;
import com.tasktracker.dto.AuthResponse;
import com.tasktracker.entity.Role;
import com.tasktracker.entity.User;
import com.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(AuthRequest request) {
        System.out.println("=== AUTH SERVICE REGISTER CALLED: " + request.getEmail() + " ===");

        // Проверяем, нет ли уже пользователя с таким email
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            System.out.println("=== USER ALREADY EXISTS: " + request.getEmail() + " ===");
            throw new RuntimeException("User already exists");
        }

        System.out.println("=== CREATING NEW USER ===");

        // Создаем нового пользователя
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);

        System.out.println("=== SAVING USER TO DB ===");
        userRepository.save(user);
        System.out.println("=== USER SAVED, ID: " + user.getId() + " ===");

        // СОЗДАЕМ UserDetails ДЛЯ JWT
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(() -> user.getRole().name())
        );

        System.out.println("=== GENERATING JWT TOKEN ===");
        String token = jwtService.generateToken(userDetails);
        System.out.println("=== TOKEN GENERATED: " +
                (token != null ? token.substring(0, Math.min(30, token.length())) : "null") + "... ===");

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        System.out.println("=== AUTH SERVICE LOGIN CALLED: " + request.getEmail() + " ===");

        try {
            // Аутентифицируем пользователя
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            System.out.println("=== AUTHENTICATION SUCCESSFUL ===");
        } catch (Exception e) {
            System.out.println("=== AUTHENTICATION FAILED: " + e.getMessage() + " ===");
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }

        // Находим пользователя
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + request.getEmail()));

        System.out.println("=== USER FOUND: " + user.getEmail() + " ===");

        // СОЗДАЕМ UserDetails ДЛЯ JWT
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(() -> user.getRole().name())
        );

        String token = jwtService.generateToken(userDetails);
        System.out.println("=== TOKEN GENERATED: " +
                (token != null ? token.substring(0, Math.min(30, token.length())) : "null") + "... ===");

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .build();
    }
}