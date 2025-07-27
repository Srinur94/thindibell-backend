package com.excelr.fooddeliveryapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="delivery_persons")
public class DeliveryPerson {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	private String phoneNumber;
	private String email;
	private String vehicleDetails;
	private Boolean isAvailable;
	private Double currentLatitude;
	private Double currentLongitude;
	
	@ManyToOne
	@JoinColumn(name = "delivery_person_id")
	private DeliveryPerson deliveryPerson;

}
