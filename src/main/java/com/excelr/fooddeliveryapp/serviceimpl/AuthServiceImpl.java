package com.excelr.fooddeliveryapp.serviceimpl;

import com.excelr.fooddeliveryapp.dto.auth.AuthRequest;
import com.excelr.fooddeliveryapp.dto.auth.AuthResponse;
import com.excelr.fooddeliveryapp.dto.UserRegisterRequestDTO; // Using this DTO for registration
import com.excelr.fooddeliveryapp.entity.User;
import com.excelr.fooddeliveryapp.enums.Role; // Import your Role enum
import com.excelr.fooddeliveryapp.exception.ResourceNotFoundException;
import com.excelr.fooddeliveryapp.repository.UserRepository;
import com.excelr.fooddeliveryapp.config.JwtService;
import com.excelr.fooddeliveryapp.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(UserRegisterRequestDTO requestDTO) {
        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        User newUser = User.builder()
                .name(requestDTO.getName())
                .email(requestDTO.getEmail())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .role(Role.CUSTOMER)
                .build();
        User savedUser = userRepository.save(newUser);

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(savedUser.getEmail())
                .password(savedUser.getPassword())
                .roles(savedUser.getRole().name())
                .build();

        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .username(savedUser.getName())
                .role(savedUser.getRole().name())
                .message("Registration successful")
                .build();
    }

    @Override
    public AuthResponse authentic(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            User user = userRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found after authentication."));

            String token = jwtService.generateToken(userDetails);

            return AuthResponse.builder()
                    .token(token)
                    .userId(user.getId())
                    .email(user.getEmail())
                    .username(user.getName())
                    .role(user.getRole().name())
                    .message("Authentication successful")
                    .build();
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    @Override
    public AuthResponse googleLogin(String googleToken) {
        throw new UnsupportedOperationException("Google login not fully implemented yet.");
    }
}
