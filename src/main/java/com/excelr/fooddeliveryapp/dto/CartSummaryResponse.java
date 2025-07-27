package com.excelr.fooddeliveryapp.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartSummaryResponse {
    private Long cartId;
    private Integer totalItems;
    private Double totalPrice;
    private String restaurantName;
    
    public CartSummaryResponse(int totalItems, double totalPrice) {
        this.totalItems = totalItems;
        this.totalPrice = totalPrice;
    }
}