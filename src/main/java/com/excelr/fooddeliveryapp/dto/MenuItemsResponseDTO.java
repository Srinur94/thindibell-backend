package com.excelr.fooddeliveryapp.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemsResponseDTO {

	private Long id;

	private String name;

	private String description;

	private BigDecimal price;

	private Long restaurantId;

	private String restaurantName;
	
	private String category;
	
	private Boolean isPopular;
	
	private String imageUrl;
}
