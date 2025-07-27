package com.excelr.fooddeliveryapp.entity;

import com.excelr.fooddeliveryapp.enums.ApplicationStatus; // Assuming you'll create this enum
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "restaurant_owner_applications")
public class RestaurantOwnerApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true) // One user can have only one pending application
    private User applicant; // The user who is applying

    @Column(nullable = false)
    private String proposedRestaurantName; // Name applicant wants for their restaurant

    @Column(nullable = false)
    private String businessRegistrationNumber; // Example: Business license number

    @Column(nullable = false)
    private String contactPhone;

    @Column(nullable = false)
    private String contactEmail;

    @Column(columnDefinition = "TEXT")
    private String additionalNotes; // Any extra info from applicant

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status; // PENDING, APPROVED, REJECTED

    @Column(nullable = false, updatable = false)
    private LocalDateTime applicationDate;

    private LocalDateTime reviewDate; // When admin reviewed it

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_admin_id")
    private User reviewedBy; // Admin who reviewed the application

    @Column(columnDefinition = "TEXT")
    private String adminComments; // Comments from the admin reviewer
}
