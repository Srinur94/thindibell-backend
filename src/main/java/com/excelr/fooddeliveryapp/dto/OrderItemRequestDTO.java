package com.excelr.fooddeliveryapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequestDTO {

	    @NotNull(message = "Menu Item Id is required")
	    private Long MenuItemId;

	    @NotNull(message = "Quantity is required")
	    @Min(value = 1, message = "Quantity must be atleast 1")
	    private Integer quantity;

	   // private Long orderId;

	   // private Double price;


}
