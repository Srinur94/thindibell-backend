package com.excelr.fooddeliveryapp.service;

import com.excelr.fooddeliveryapp.enums.PaymentMethod;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentGatewayService {
    
    String processPayment(BigDecimal amount, PaymentMethod paymentMethod, Map<String, String> paymentDetails);
    
    String refundPayment(String transactionId, BigDecimal amount, String reason);
    
    boolean verifyPayment(String transactionId);
}