package com.excelr.fooddeliveryapp.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne; // Ensure ManyToOne
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="restaurants")
public class Restaurant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name="name", nullable=false)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY) // LAZY is generally good, but ensure it's fetched for display
	@JoinColumn(name="owner_id", nullable = false) // Assuming foreign key column is 'owner_id'
	private User owner; // This is the correct way to link to the owner

	// CRITICAL: Ensure NO 'ownerName' field exists here.
	// Example of what NOT to have:
	// @Column(name="owner_name")
	// private String ownerName;

	@Column(nullable = false)
    private String address;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(name = "image_url")
	private String imageUrl;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "rating")
	private double rating;

	@OneToMany(mappedBy = "restaurant", cascade=CascadeType.ALL, orphanRemoval = true)
	private List<MenuItem> menuItems;

	@OneToMany(mappedBy = "restaurant", cascade=CascadeType.ALL)
	private List<Reviews> reviews;
}
