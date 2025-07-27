package com.excelr.fooddeliveryapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartItemRequestDTO {
    @NotNull(message = "Cart Item ID cannot be null")
    private Long cartItemId;

    @Min(value = 0, message = "Quantity cannot be negative")
    private int quantity;
}