package com.excelr.fooddeliveryapp.controller;



import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.excelr.fooddeliveryapp.dto.PaymentRequestDTO;
import com.excelr.fooddeliveryapp.dto.PaymentResponseDTO;
import com.excelr.fooddeliveryapp.dto.PaymentStatusDTO;
import com.excelr.fooddeliveryapp.entity.Payment;
import com.excelr.fooddeliveryapp.enums.PaymentStatus;
import com.excelr.fooddeliveryapp.repository.PaymentRepository;
import com.excelr.fooddeliveryapp.service.PaymentService;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    
    private final PaymentRepository paymentRepository;

    @PostMapping("/create")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')") // Ensure only CUSTOMER/ADMIN can create payments
    public ResponseEntity<PaymentResponseDTO> createPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        // Log the incoming request payload for debugging
        System.out.println("Backend: Received payment creation request for Order ID: " + paymentRequestDTO.getOrderId());
        System.out.println("Backend: Payment Method: " + paymentRequestDTO.getPaymentMethod());
        System.out.println("Backend: Amount: " + paymentRequestDTO.getAmount());

        // Call your service layer to handle payment creation logic
        PaymentResponseDTO response = paymentService.createPayment(paymentRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{paymentId}/process")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PaymentResponseDTO> processPayment(
            @PathVariable Long paymentId,
            @Valid @RequestBody PaymentRequestDTO paymentRequestDTO) {
        PaymentResponseDTO response = paymentService.processPayment(paymentId, paymentRequestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long paymentId) {
        PaymentResponseDTO response = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{paymentId}/status")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<PaymentStatusDTO> getPaymentStatus(@PathVariable Long paymentId) {
        PaymentStatusDTO response = paymentService.getPaymentStatus(paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByOrder(@PathVariable Long orderId) {
        List<PaymentResponseDTO> response = paymentService.getPaymentsByOrder(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("@securityService.isUserOrAdmin(#userId)")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByUser(@PathVariable Long userId) {
        List<PaymentResponseDTO> response = paymentService.getPaymentsByUser(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{paymentId}/refund")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> refundPayment(
            @PathVariable Long paymentId,
            @RequestBody Map<String, String> request
    ) {
        Optional<Payment> optionalPayment = paymentRepository.findById(paymentId);

        if (optionalPayment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Payment not found");
        }

        Payment payment = optionalPayment.get();

        if (!payment.getStatus().equals(PaymentStatus.COMPLETED)) {
            return ResponseEntity.badRequest().body("Only completed payments can be refunded");
        }

        // Simulate refund
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundReason(request.get("reason"));
        payment.setRefundedAt(LocalDateTime.now());
        payment.setRefundTransactionId("mock_refund_txn_" + UUID.randomUUID());

        paymentRepository.save(payment);

        return ResponseEntity.ok("Refund processed successfully");
    }
}