package com.excelr.fooddeliveryapp.controller;

import com.excelr.fooddeliveryapp.dto.*;
import com.excelr.fooddeliveryapp.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus; // Import HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid; // Import Valid for DTO validation
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin") // Base path for admin-specific endpoints
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // All methods in this controller require ADMIN role
public class AdminController {

    private final OrderService orderService;
    private final UserService userService;
    private final RestaurantService restaurantService;
    private final MenuItemsService menuItemService;

    @GetMapping("/dashboard/summary")
    public ResponseEntity<DashboardSummaryDTO> getSummary() {
        DashboardSummaryDTO summary = DashboardSummaryDTO.builder()
                .totalUsers(userService.getAllUsers().size())
                .totalOrders(orderService.getAllOrders().size())
                .totalRestaurants(restaurantService.getAllRestaurants().size())
                .totalMenuItems(menuItemService.getAllMenuItems().size())
                .build();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
    
    @GetMapping("/restaurants/all")
    public ResponseEntity<List<RestaurantResponseDTO>> getAllRestaurants(){
    	return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @PostMapping("/restaurants") 
    public ResponseEntity<RestaurantResponseDTO> createRestaurant(@Valid @RequestBody RestaurantRequestDTO restaurantRequestDTO) {
        RestaurantResponseDTO createdRestaurant = restaurantService.createRestaurant(restaurantRequestDTO);
        return new ResponseEntity<>(createdRestaurant, HttpStatus.CREATED);
    }

    
    @PutMapping("/restaurants/{id}") 
    public ResponseEntity<RestaurantResponseDTO> updateRestaurant(
            @PathVariable Long id,
            @Valid @RequestBody RestaurantRequestDTO restaurantRequestDTO) {
        RestaurantResponseDTO updatedRestaurant = restaurantService.updateRestaurant(id, restaurantRequestDTO);
        return ResponseEntity.ok(updatedRestaurant);
    }
    
    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDto) {
        UserDTO updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/restaurants/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }
    
 // --- Menu Item Management (CRITICAL ENDPOINTS) ---
//
//    // Get all menu items for a specific restaurant (used by AdminMenuItems.jsx)
//    @GetMapping("/menu-items/restaurant/{restaurantId}") // This is the endpoint AdminMenuItems.jsx calls
//    @PreAuthorize("hasRole('ADMIN')") // Must be accessible by ADMIN
//    public ResponseEntity<List<MenuItemsResponseDTO>> getMenuItemsByRestaurantId(@PathVariable Long restaurantId) {
//        List<MenuItemsResponseDTO> menuItems = menuItemService.getMenuItemsByRestaurant(restaurantId);
//        return ResponseEntity.ok(menuItems);
//    }
//
//    // Create a new menu item
//    @PostMapping("/menu-items")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<MenuItemsResponseDTO> createMenuItem(@Valid @RequestBody MenuItemsRequestDTO menuItemRequestDTO) {
//        MenuItemsResponseDTO response = menuItemService.createMenuItems(menuItemRequestDTO);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    // Update an existing menu item
//    @PutMapping("/menu-items/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<MenuItemsResponseDTO> updateMenuItem(@PathVariable Long id, @Valid @RequestBody MenuItemsRequestDTO menuItemRequestDTO) {
//        MenuItemsResponseDTO updatedMenuItem = menuItemService.updateMenuItem(id, menuItemRequestDTO);
//        return ResponseEntity.ok(updatedMenuItem);
//    }
//
//    // Delete a menu item
//    @DeleteMapping("/menu-items/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
//        menuItemService.deleteMenuItem(id);
//        return ResponseEntity.noContent().build();
//    }
}
