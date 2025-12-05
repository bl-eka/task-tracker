package com.tasktracker.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dto-test")
public class TestDTOController {

    @PostMapping("/string")
    public String testString(@RequestBody String body) {
        System.out.println("String body: " + body);
        return "Received: " + body;
    }

    @PostMapping("/object")
    public String testObject(@RequestBody Object body) {
        System.out.println("Object body: " + body);
        return "Received object";
    }
}