package com.excelr.fooddeliveryapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailsDTO {
    @NotBlank(message = "Stripe Payment Method ID is required")
    private String stripePaymentMethodId;

    @NotBlank(message = "Currency is required")
    private String currency;
}