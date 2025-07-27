package com.excelr.fooddeliveryapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartRequest {

	private Long userId;
	
    @NotNull(message = "Menu item ID is required")
    private Long menuItemId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}
