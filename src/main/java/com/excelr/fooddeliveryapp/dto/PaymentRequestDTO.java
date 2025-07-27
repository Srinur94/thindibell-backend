package com.excelr.fooddeliveryapp.dto;

import java.math.BigDecimal;
import java.util.Map;

import com.excelr.fooddeliveryapp.enums.PaymentMethod;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequestDTO {
    
    @NotNull(message = "Order ID is required")
    @Positive(message = "Order ID must be positive")
    private Long orderId;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
    
    @NotNull(message = "Amount is required") 
    private BigDecimal amount;
    
    
   
    
    @Valid // This annotation is important for validating fields inside PaymentDetailsDTO
    @NotNull(message = "Payment details are required")
    private PaymentDetailsDTO paymentDetails;
//    public Long getOrderId() {
//        return orderId;
//    }
//
//    public void setOrderId(Long orderId) {
//        this.orderId = orderId;
//    }
//
//    public PaymentMethod getPaymentMethod() {
//        return paymentMethod;
//    }
//
//    public void setPaymentMethod(PaymentMethod paymentMethod) {
//        this.paymentMethod = paymentMethod;
//    }
//
//    public Map<String, String> getPaymentDetails() {
//        return paymentDetails;
//    }
//
//    public void setPaymentDetails(Map<String, String> paymentDetails) {
//        this.paymentDetails = paymentDetails;
//    }

    
}
