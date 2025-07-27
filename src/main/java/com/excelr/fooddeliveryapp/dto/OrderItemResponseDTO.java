package com.excelr.fooddeliveryapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponseDTO {

	private Long id;
	private Long orderId;
	private Long menuItemId;
	private String menuItemName;
	private Integer quantity;
	private Double price;

}
