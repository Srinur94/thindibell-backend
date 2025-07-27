package com.excelr.fooddeliveryapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantOwnerApplicationResponseDTO {
    private Long id;
    private Long applicantId;
    private String applicantName; // The name of the user who applied
    private String applicantEmail; // The email of the user who applied
    private String proposedRestaurantName;
    private String businessRegistrationNumber;
    private String contactPhone;
    private String contactEmail;
    private String additionalNotes;
    private String status; // PENDING, APPROVED, REJECTED
    private LocalDateTime applicationDate;
    private LocalDateTime reviewDate;
    private Long reviewedByAdminId;
    private String reviewedByAdminName;
    private String adminComments;
}
