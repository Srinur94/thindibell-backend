package com.excelr.fooddeliveryapp.dto;

import com.excelr.fooddeliveryapp.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
	 private Long orderId;
     private Long userId;
     private Long restaurantId;
     private String restaurantName;
     private String deliveryAddress; // Full address string for display
     private LocalDateTime orderDate;
     private Double totalAmount;
     private String paymentMethod;
     private String status; // e.g., PENDING, CONFIRMED, DELIVERED, CANCELLED
     private Boolean noContactDelivery;
     private String couponCode;
     private List<OrderItemDTO> orderItems;

    
//    public OrderResponseDTO orderItems(List<OrderItemResponseDTO> items) {
//        this.orderItems = items;
//        return this;
//    }

}