package com.excelr.fooddeliveryapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Link to the User entity

    private String name; // e.g., "Home", "Work"
    private String fullAddress; // Full display address
    private String doorNo; // CRITICAL: New field
    private String street;
    private String landmark; // CRITICAL: New field
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private Double latitude;
    private Double longitude;

    // You might want to add a default constructor if you have other constructors
    // public Address() {}
}
