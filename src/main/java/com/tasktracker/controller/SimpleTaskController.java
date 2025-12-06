package com.tasktracker.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/simple-tasks")
@Slf4j
public class SimpleTaskController {

    @GetMapping
    public ResponseEntity<String> testAuth(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok("User is ANONYMOUS");
        }
        log.info("User authenticated: {}, Authorities: {}",
                userDetails.getUsername(), userDetails.getAuthorities());
        return ResponseEntity.ok("Hello " + userDetails.getUsername());
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createSimpleTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request) {

        log.info("Creating task for user: {}", userDetails.getUsername());
        log.info("Request body: {}", request);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Task created for " + userDetails.getUsername(),
                "title", request.get("title"),
                "owner", userDetails.getUsername()
        ));
    }
}