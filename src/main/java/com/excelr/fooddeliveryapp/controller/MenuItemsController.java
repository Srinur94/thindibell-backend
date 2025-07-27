package com.excelr.fooddeliveryapp.controller;

import com.excelr.fooddeliveryapp.dto.MenuItemsRequestDTO;
import com.excelr.fooddeliveryapp.dto.MenuItemsResponseDTO;
import com.excelr.fooddeliveryapp.service.MenuItemsService; // Note: You use MenuItemsService
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu-items")
@Validated
@RequiredArgsConstructor
public class MenuItemsController {

    private final MenuItemsService menuItemsService;

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')") // Corrected syntax for multiple roles
    public ResponseEntity<MenuItemsResponseDTO> createMenuItems(@Valid @RequestBody MenuItemsRequestDTO dto) {
        return ResponseEntity.status(201).body(menuItemsService.createMenuItems(dto));
    }

    // CRITICAL FIX: Add this method to handle GET /api/v1/menu-items
    @GetMapping // Maps to GET /api/v1/menu-items
    public ResponseEntity<List<MenuItemsResponseDTO>> getAllMenuItems() {
        return ResponseEntity.ok(menuItemsService.getAllMenuItems()); // Assuming this method exists in your service
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_OWNER')")
    public ResponseEntity<MenuItemsResponseDTO> getMenuItemsById(@PathVariable Long id) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsById(id));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItemsResponseDTO>> getMenuItemsByRestaurant(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuItemsService.getMenuItemsByRestaurant(restaurantId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_OWNER')")
    public ResponseEntity<MenuItemsResponseDTO> updateMenuItem(@PathVariable Long id, @Valid @RequestBody MenuItemsRequestDTO dto) {
        return ResponseEntity.ok(menuItemsService.updateMenuItem(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_OWNER')")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuItemsService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}
