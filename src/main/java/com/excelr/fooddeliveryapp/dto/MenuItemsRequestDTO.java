package com.excelr.fooddeliveryapp.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemsRequestDTO {

	@NotBlank(message = "Menu item name is required")
	private String name;

	private String description;

	@NotNull(message = "Price is required")
	@Positive(message = "Price must be a positive value")
	private BigDecimal price;

	@NotNull(message = "Restaurant Id is required")
	private Long restaurantId;
	
	private String category;
	private Boolean isPopular;
	private String imageUrl;

}
