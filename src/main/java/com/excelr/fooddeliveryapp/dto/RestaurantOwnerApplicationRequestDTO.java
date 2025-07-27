package com.excelr.fooddeliveryapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantOwnerApplicationRequestDTO {
    @NotNull(message = "Applicant User ID is required")
    private Long applicantId; // The ID of the user submitting the application

    @NotBlank(message = "Proposed restaurant name is required")
    private String proposedRestaurantName;

    @NotBlank(message = "Business registration number is required")
    private String businessRegistrationNumber;

    @NotBlank(message = "Contact phone number is required")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Contact phone number must be 10 or 11 digits")
    private String contactPhone;

    @NotBlank(message = "Contact email is required")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Invalid email format")
    private String contactEmail;

    private String additionalNotes; // Optional
}
