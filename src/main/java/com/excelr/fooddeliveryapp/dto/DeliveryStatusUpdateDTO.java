package com.excelr.fooddeliveryapp.dto;

import com.excelr.fooddeliveryapp.enums.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryStatusUpdateDTO {

	@NotNull(message = "Order id is required")
	private Long orderId;

	@NotNull(message = "Status is required")
	private OrderStatus status;

}
