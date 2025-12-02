package com.tasktracker.security;

import org.springframework.stereotype.Service;

@Service
public class SimpleTokenService {
    public String generateToken(String email) {
        // Простая заглушка
        return "simple-token-" + email + "-" + System.currentTimeMillis();
    }
}