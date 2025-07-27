package com.excelr.fooddeliveryapp.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateRequestDTO {

	@NotNull(message = "Restaurant id is required")
	private Long restaurantId;

	@NotEmpty(message = "Order must contain at least one item")
	@Valid
	private List<OrderItemRequestDTO> items;

}
