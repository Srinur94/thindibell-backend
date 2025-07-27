package com.excelr.fooddeliveryapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationUpdateDTO {
 
	@NotNull(message = "Latitude is required")
	private Double latitude;
	
	@NotNull(message = "Longitude is required")
	private Double longitude;
}
