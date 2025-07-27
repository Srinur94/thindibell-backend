package com.excelr.fooddeliveryapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemsDTO {
	    private Long id;

	    @NotBlank(message = "Item name is required")
	    private String name;

	    private String description;

	    @NotNull(message = "Price is required")
	    @Positive(message = "Price must be positive")
	    private Double price;

	    @NotNull(message = "Restaurant id is required")
	    private Long restaurantId;
}
