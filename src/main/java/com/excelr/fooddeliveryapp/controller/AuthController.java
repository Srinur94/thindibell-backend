package com.excelr.fooddeliveryapp.controller;

import com.excelr.fooddeliveryapp.dto.auth.AuthRequest;
import com.excelr.fooddeliveryapp.dto.auth.AuthResponse;
import com.excelr.fooddeliveryapp.dto.UserRegisterRequestDTO; // Ensure this is the DTO used for registration
import com.excelr.fooddeliveryapp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody UserRegisterRequestDTO requestDTO) { // Use UserRegisterRequestDTO
        AuthResponse registeredUserResponse = authService.register(requestDTO);
        return new ResponseEntity<>(registeredUserResponse, HttpStatus.CREATED);
    }

    @PostMapping("/authenticate") // Assuming this is your login endpoint
    public ResponseEntity<AuthResponse> authenticUser(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = authService.authentic(authRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/google-login")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody String googleToken) {
        AuthResponse authResponse = authService.googleLogin(googleToken);
        return ResponseEntity.ok(authResponse);
    }
}
