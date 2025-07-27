package com.excelr.fooddeliveryapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantRequestDTO {
    @NotBlank(message = "Restaurant name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Phone number must be 10 or 11 digits")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Owner ID is required")
    private Long ownerId; // You send ownerId, not ownerName

    private String imageUrl;

    @Min(value = 0, message = "Rating must be at least 0.0")
    @Max(value = 5, message = "Rating cannot exceed 5.0")
    private double rating;

    private String description;

    // CRITICAL: Ensure NO 'ownerName' field exists here.
    // private String ownerName; // DO NOT HAVE THIS LINE
}
