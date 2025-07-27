package com.excelr.fooddeliveryapp.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    private Long cartItemId;
    private Long menuItemId;
    private String menuItemName;
    private String menuItemImageUrl;
    private BigDecimal menuItemPrice;
    private int quantity;
    private BigDecimal subtotal;
    private Long restaurantId;
    private String restaurantName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
