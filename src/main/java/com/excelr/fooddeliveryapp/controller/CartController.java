package com.excelr.fooddeliveryapp.controller;

import com.excelr.fooddeliveryapp.dto.AddToCartRequest;
import com.excelr.fooddeliveryapp.dto.CartDTO;
import com.excelr.fooddeliveryapp.dto.CartResponse;
import com.excelr.fooddeliveryapp.dto.CartSummaryResponse;
import com.excelr.fooddeliveryapp.dto.UpdateCartItemRequest;
import com.excelr.fooddeliveryapp.entity.User; // Import your User entity
import com.excelr.fooddeliveryapp.repository.UserRepository;
import com.excelr.fooddeliveryapp.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    // Helper method to get User entity from UserDetails
    // This ensures we always get the ID of the currently authenticated user
    private User getUserFromPrincipal(UserDetails userDetails) {
        String email = userDetails.getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartDTO> addMenuItemToCart(
            @RequestBody AddToCartRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = getUserFromPrincipal(userDetails);
        CartDTO cartDTO = cartService.addToCart(user.getId(), request);
        return ResponseEntity.ok(cartDTO);
    }
    @PutMapping("/update/{cartItemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartDTO> updateCartItem(
            @PathVariable Long cartItemId, // Get cartItemId from path variable
            @RequestBody UpdateCartItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = getUserFromPrincipal(userDetails);
        // FIX HERE: Pass cartItemId from @PathVariable directly to the service
        CartDTO updatedCart = cartService.updateCartItem(user.getId(), cartItemId, request);
        return ResponseEntity.ok(updatedCart);
    }


    @DeleteMapping("/remove/{cartItemId}")
    @PreAuthorize("hasRole('CUSTOMER')") // Ensure this is present for security
    public ResponseEntity<CartDTO> removeItem(
            @PathVariable Long cartItemId,
            @AuthenticationPrincipal UserDetails userDetails) { // <--- ADDED: Get authenticated user details
        
        User user = getUserFromPrincipal(userDetails); // Get User entity to access ID
        // Pass the userId from the authenticated user to the service method
        CartDTO updatedCart = cartService.removeFromCart(user.getId(), cartItemId); // <--- FIXED: Passing userId
        return ResponseEntity.ok(updatedCart);
    }

    @GetMapping // This endpoint is for fetching the cart for the authenticated user
    @PreAuthorize("hasRole('CUSTOMER')") // Ensure this is present for security
    public ResponseEntity<CartDTO> getCartForUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromPrincipal(userDetails);
        CartDTO cartDTO = cartService.getCart(user.getId());
        return ResponseEntity.ok(cartDTO);
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('CUSTOMER')") // Ensure this is present for security
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetails userDetails) { // <--- ADDED: Get authenticated user details
        User user = getUserFromPrincipal(userDetails);
        cartService.clearCart(user.getId()); // <--- FIXED: Passing userId
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('CUSTOMER')") // Ensure this is present for security
    public ResponseEntity<CartSummaryResponse> getCartSummary(@AuthenticationPrincipal UserDetails userDetails) { // <--- ADDED: Get authenticated user details
        User user = getUserFromPrincipal(userDetails);
        CartSummaryResponse summary = cartService.getCartSummary(user.getId()); // <--- FIXED: Passing userId
        return ResponseEntity.ok(summary);
    }
}