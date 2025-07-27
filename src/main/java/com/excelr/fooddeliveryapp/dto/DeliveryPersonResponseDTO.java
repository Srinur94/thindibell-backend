package com.excelr.fooddeliveryapp.dto;

import java.util.List;

import com.excelr.fooddeliveryapp.entity.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryPersonResponseDTO {

	private Long id;
	private String name;
	private String phoneNumber;
	private String email;
	private List<Order> assignedOrders;
	private Boolean isAvailable;
	private Double currentLatitude;
	private Double currentLongitude;
}
