package com.excelr.fooddeliveryapp.dto;


import java.time.LocalDateTime;

import com.excelr.fooddeliveryapp.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryStatusDTO {
	    private Long orderId;
	    private OrderStatus status;
	    private LocalDateTime timestamp;
}
