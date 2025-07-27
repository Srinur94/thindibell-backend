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
public class OrderItemDTO {

	 private Long id;
	    private Long menuItemId;
	    private String menuItemName;
	    private int quantity;
	    private BigDecimal price; 
}



