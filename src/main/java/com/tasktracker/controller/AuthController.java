package com.tasktracker.controller;

import com.tasktracker.dto.AuthRequest;
import com.tasktracker.dto.AuthResponse;
import com.tasktracker.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Регистрация и вход в систему")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Регистрация нового пользователя",
            description = "Создает нового пользователя в системе")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        System.out.println("=== REGISTER CALLED: " + request.getEmail() + " ===");
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "Вход в систему",
            description = "Аутентификация пользователя и получение JWT токена")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        System.out.println("=== LOGIN CALLED: " + request.getEmail() + " ===");
        return ResponseEntity.ok(authService.login(request));
    }
}