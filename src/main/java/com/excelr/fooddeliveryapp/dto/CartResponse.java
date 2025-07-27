package com.excelr.fooddeliveryapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private Long cartId;
    private Long userId;
    private Long restaurantId;
    private String restaurantName;
    private BigDecimal totalAmount;
    private List<CartItemResponse> items;
}
