package com.excelr.fooddeliveryapp.dto;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequestDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Address name is required")
    private String name;

    @NotBlank(message = "Full address is required")
    private String fullAddress;

    @NotBlank(message = "Door No. is required") // CRITICAL: New field validation
    private String doorNo;

    @NotBlank(message = "Street is required")
    private String street;

    @NotBlank(message = "Landmark is required") // CRITICAL: New field validation
    private String landmark;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Zip Code is required")
    @Pattern(regexp = "^\\d{6}$", message = "Zip Code must be 6 digits")
    private String zipCode;

    @NotBlank(message = "Country is required")
    private String country;

    private Double latitude;
    private Double longitude;
}
