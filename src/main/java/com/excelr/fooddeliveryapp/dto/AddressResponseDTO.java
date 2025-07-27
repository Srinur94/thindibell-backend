package com.excelr.fooddeliveryapp.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponseDTO {
    private Long id;
    private Long userId;
    private String name;
    private String fullAddress;
    private String doorNo; // CRITICAL: New field
    private String street;
    private String landmark; // CRITICAL: New field
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private Double latitude;
    private Double longitude;
}
