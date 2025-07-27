package com.excelr.fooddeliveryapp.service;


import java.util.List;

import com.excelr.fooddeliveryapp.dto.PaymentRequestDTO;
import com.excelr.fooddeliveryapp.dto.PaymentResponseDTO;
import com.excelr.fooddeliveryapp.dto.PaymentStatusDTO;

public interface PaymentService {
    
    PaymentResponseDTO createPayment(PaymentRequestDTO paymentRequestDTO);
    
    PaymentResponseDTO processPayment(Long paymentId, PaymentRequestDTO paymentRequestDTO);
    
    PaymentStatusDTO getPaymentStatus(Long paymentId);
    
    PaymentResponseDTO getPaymentById(Long paymentId);
    
    List<PaymentResponseDTO> getPaymentsByOrder(Long orderId);
    
    List<PaymentResponseDTO> getPaymentsByUser(Long userId);
    
    PaymentResponseDTO refundPayment(Long paymentId, String reason);
    
    PaymentResponseDTO cancelPayment(Long paymentId);
}

