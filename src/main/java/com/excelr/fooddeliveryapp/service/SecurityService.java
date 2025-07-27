package com.excelr.fooddeliveryapp.service;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.excelr.fooddeliveryapp.entity.User;
import com.excelr.fooddeliveryapp.enums.Role;
import com.excelr.fooddeliveryapp.exception.ResourceNotFoundException;
import com.excelr.fooddeliveryapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service("securityService") // Give it a name for SpEL
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;

    public boolean isUserOrAdmin(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName(); // This is the email in our setup

        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found."));

        if (currentUser.getRole().equals(Role.ADMIN)) {
            return true; // Admin can access any user's data
        }
        return currentUser.getId().equals(userId); // User can only access their own data
    }

    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName(); // This is the email in our setup

        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found."));

        return currentUser.getId().equals(userId);
    }

    public boolean isRestaurantOwner(Long restaurantId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found."));

        if (!currentUser.getRole().equals(Role.RESTAURANT_OWNER) && !currentUser.getRole().equals(Role.ADMIN)) {
            return false;
        }

        return true; // You need to implement actual logic using RestaurantRepository
    }

    public boolean isOrderCustomerOrAdmin(Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found."));

        if (currentUser.getRole().equals(Role.ADMIN)) {
            return true;
        }


        return true; // Placeholder
    }

}