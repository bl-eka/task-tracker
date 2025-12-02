package com.tasktracker.controller;

import com.tasktracker.dto.AuthRequest;
import com.tasktracker.dto.AuthResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/register")
    public AuthResponse register(@RequestBody AuthRequest request) {
        return new AuthResponse("temp-token", request.getEmail(), "USER");
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return new AuthResponse("temp-token", request.getEmail(), "USER");
    }
}