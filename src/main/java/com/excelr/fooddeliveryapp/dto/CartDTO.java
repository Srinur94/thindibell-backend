package com.excelr.fooddeliveryapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {
    private Long cartId;
    private Long userId;
    private List<CartItemDTO> items;
    private Long restaurantId;  
    private BigDecimal totalPrice;
}
