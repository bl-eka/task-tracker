package com.tasktracker.service;

import com.tasktracker.dto.AuthRequest;
import com.tasktracker.dto.AuthResponse;
import com.tasktracker.entity.Role;
import com.tasktracker.entity.User;
import com.tasktracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        System.out.println("=== GENERATING JWT TOKEN ===");
        String token = jwtService.generateToken(user);
        System.out.println("=== TOKEN GENERATED: " + token.substring(0, 30) + "... ===");

        // ВОТ ИСПРАВЛЕНИЕ: возвращаем и токен, и email
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        System.out.println("=== AUTH SERVICE LOGIN CALLED: " + request.getEmail() + " ===");

        // Аутентифицируем пользователя
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        System.out.println("=== AUTHENTICATION SUCCESSFUL ===");

        // Находим пользователя
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("=== USER FOUND: " + user.getEmail() + " ===");

        String token = jwtService.generateToken(user);
        System.out.println("=== TOKEN GENERATED: " + token.substring(0, 30) + "... ===");

        // ВОТ ИСПРАВЛЕНИЕ: возвращаем и токен, и email
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .build();
    }
}