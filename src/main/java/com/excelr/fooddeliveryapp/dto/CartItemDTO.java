package com.excelr.fooddeliveryapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {
    private Long cartItemId;
    private Long menuItemId; // IMPORTANT CHANGE: Renamed from productId
    private String menuItemName;
    private String menuItemImageUrl;
    private BigDecimal menuItemPrice;
    private Integer quantity;
    private BigDecimal subtotal; // quantity * menuItemPrice
    private Long restaurantId; // Added for convenience
    private String restaurantName; // Added for convenience
}