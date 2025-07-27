package com.excelr.fooddeliveryapp.controller;

import com.excelr.fooddeliveryapp.dto.RestaurantOwnerApplicationRequestDTO;
import com.excelr.fooddeliveryapp.dto.RestaurantOwnerApplicationResponseDTO;
import com.excelr.fooddeliveryapp.entity.User;
import com.excelr.fooddeliveryapp.enums.ApplicationStatus;
import com.excelr.fooddeliveryapp.service.RestaurantOwnerApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // For role-based access
import org.springframework.security.core.Authentication; // To get current user's info
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/applications/restaurant-owner")
@RequiredArgsConstructor
public class RestaurantOwnerApplicationController {

    private final RestaurantOwnerApplicationService applicationService;

    @PostMapping("/submit")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')") // Only customers can submit
    public ResponseEntity<RestaurantOwnerApplicationResponseDTO> submitApplication(
            @Valid @RequestBody RestaurantOwnerApplicationRequestDTO requestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
       
        RestaurantOwnerApplicationResponseDTO response = applicationService.submitApplication(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')") // Only admins can view all
    public ResponseEntity<List<RestaurantOwnerApplicationResponseDTO>> getAllApplications() {
        List<RestaurantOwnerApplicationResponseDTO> applications = applicationService.getAllApplications();
        return ResponseEntity.ok(applications);
    }

    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<RestaurantOwnerApplicationResponseDTO>> getApplicationsByStatus(@PathVariable ApplicationStatus status) {
        List<RestaurantOwnerApplicationResponseDTO> applications = applicationService.getApplicationsByStatus(status);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RestaurantOwnerApplicationResponseDTO> getApplicationById(@PathVariable Long id) {
        RestaurantOwnerApplicationResponseDTO application = applicationService.getApplicationById(id);
        return ResponseEntity.ok(application);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RestaurantOwnerApplicationResponseDTO> approveApplication(
            @PathVariable Long id,
            @RequestParam String adminComments) { 
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long adminId = ((User) authentication.getPrincipal()).getId(); 

        RestaurantOwnerApplicationResponseDTO response = applicationService.approveApplication(id, adminId, adminComments);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RestaurantOwnerApplicationResponseDTO> rejectApplication(
            @PathVariable Long id,
            @RequestParam String adminComments) { 
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long adminId = ((User) authentication.getPrincipal()).getId(); 

        RestaurantOwnerApplicationResponseDTO response = applicationService.rejectApplication(id, adminId, adminComments);
        return ResponseEntity.ok(response);
    }
}
