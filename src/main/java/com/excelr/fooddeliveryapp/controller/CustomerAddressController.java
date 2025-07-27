package com.excelr.fooddeliveryapp.controller;

import com.excelr.fooddeliveryapp.dto.AddressRequestDTO;
import com.excelr.fooddeliveryapp.dto.AddressResponseDTO;
import com.excelr.fooddeliveryapp.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerAddressController {

    private final AddressService addressService;

    @PostMapping("/{userId}/addresses")
    @PreAuthorize("hasRole('CUSTOMER') and #userId == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<AddressResponseDTO> addAddress(@PathVariable Long userId, @Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        AddressResponseDTO newAddress = addressService.addAddress(userId, addressRequestDTO);
        return ResponseEntity.status(201).body(newAddress);
    }

    @GetMapping("/{userId}/addresses")
    @PreAuthorize("hasRole('CUSTOMER') and #userId == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<List<AddressResponseDTO>> getUserAddresses(@PathVariable Long userId) {
        List<AddressResponseDTO> addresses = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(addresses);
    }

    // CRITICAL FIX: Endpoint to update an existing address
    @PutMapping("/{userId}/addresses/{addressId}")
    @PreAuthorize("hasRole('CUSTOMER') and #userId == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<AddressResponseDTO> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        AddressResponseDTO updatedAddress = addressService.updateAddress(userId, addressId, addressRequestDTO);
        return ResponseEntity.ok(updatedAddress);
    }

    // CRITICAL FIX: Endpoint to delete an address
    @DeleteMapping("/{userId}/addresses/{addressId}")
    @PreAuthorize("hasRole('CUSTOMER') and #userId == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // Example: Get Customer Profile (keeping for context)
    @GetMapping("/{userId}/profile")
    @PreAuthorize("hasRole('CUSTOMER') and #userId == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<String> getCustomerProfile(@PathVariable Long userId) {
        return ResponseEntity.ok("Customer profile for ID: " + userId); // Placeholder
    }
}
