package com.excelr.fooddeliveryapp.dto;

import java.util.List;

import com.excelr.fooddeliveryapp.entity.Order;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryPersonRequestDTO {

	@NotBlank(message = "Name is required")
	 private String name;
	 
	@NotBlank(message = "Phone number is required")
	@Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
	 private String phoneNumber;
	 
	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	 private String email;
	 
	@NotBlank(message = "Vehicle details is required")
	private String vehicleDetails;
	
	@NotNull(message = "Availability status is required")
	private Boolean isAvailable;

	private List<Order> assignedOrders;
	
	private Double currentLatitude;
	private Double currentLongitude;
	 
	 
}
