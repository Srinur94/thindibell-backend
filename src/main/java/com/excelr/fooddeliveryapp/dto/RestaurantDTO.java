package com.excelr.fooddeliveryapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDTO {


	   @NotBlank(message = "Restaurant name is required")
	    private String name;

	   @NotNull(message="Location is required")
	    private String location;

        @NotNull(message = "Required owner id")
	    private Long ownerId;

	    private String description;

}
