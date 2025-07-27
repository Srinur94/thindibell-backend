package com.excelr.fooddeliveryapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantResponseDTO {
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;

    private Long ownerId;
    private String ownerName; // This is correct for frontend display

    private String imageUrl; // CRITICAL: This must be camelCase
    private double rating;
    private String description;
}
