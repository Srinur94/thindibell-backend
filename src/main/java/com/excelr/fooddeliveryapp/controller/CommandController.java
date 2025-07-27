package com.excelr.fooddeliveryapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class CommandController {

    @PostMapping("/command")
    public ResponseEntity<String> handleCommand(@RequestBody Map<String, String> body) {
        String command = body.get("command");
        return ResponseEntity.ok("Received command: " + command);
    }
}
